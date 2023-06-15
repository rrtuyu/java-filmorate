package ru.yandex.practicum.filmorate.storage.film.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDao;
import ru.yandex.practicum.filmorate.storage.like.LikeDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component("filmStorageDB")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDao genreDao;
    private final LikeDao likeDao;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate,
                         GenreDao genreDao,
                         LikeDao likeDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDao = genreDao;
        this.likeDao = likeDao;
    }

    @Override
    public Film addFilm(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("rating_id", film.getMpa().getId());
        values.put("duration", film.getDuration());

        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");

        try {
            int generatedId = insert.executeAndReturnKey(values).intValue();
            film.setId(generatedId);
            film.setGenres(batchUpdateGenres(film));
            return film;
        } catch (RuntimeException e) {
            throw new ValidationException(String.format("Failed to add film: %s", film));
        }
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films " +
                "SET name = ?, description = ?, release_date = ?, rating_id = ?, duration = ? " +
                "WHERE id = ?";

        try {
            jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(),
                    film.getMpa().getId(), film.getDuration(), film.getId());
            genreDao.clearGenreById(film.getId());
            film.setGenres(batchUpdateGenres(film));
            return film;
        } catch (RuntimeException e) {
            throw new ValidationException(String.format("Failed to update film: %s", film));
        }
    }

    @Override
    public boolean hasFilm(Integer id) {
        String sql = "SELECT EXISTS(SELECT FROM films WHERE id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, id));
    }

    @Override
    public Optional<Film> getFilm(Integer id) {
        try {
            String sql = "SELECT f.*, r.name rating_name, r.description rating_description " +
                    "FROM films f JOIN rating r ON f.rating_id = r.id WHERE f.id = ?";
            Optional<Film> filmOpt = Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::makeFilm, id));
            if (filmOpt.isPresent()) {
                List<Genre> genres = new ArrayList<>(genreDao.getGenresOfFilm(id));
                filmOpt.get().setGenres(genres);
                return filmOpt;
            } else {
                return Optional.empty();
            }
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Film> getAllFilms() {
        String sql = "SELECT f.*, r.name rating_name, r.description rating_description " +
                "FROM films f JOIN rating r ON f.rating_id = r.id";
        Collection<Film> films = jdbcTemplate.query(sql, this::makeFilm);
        Map<Integer, Collection<Genre>> genres = genreDao.getFilmGenreMap(films);
        for (Film film : films)
            film.setGenres(new ArrayList<>(genres.getOrDefault(film.getId(), Collections.emptyList())));

        return films;
    }

    @Override
    public Collection<Film> getPopular(int count) {
        String sql = "SELECT f.*, r.name rating_name, r.description rating_description " +
                "FROM films f JOIN rating r ON f.rating_id = r.id " +
                "LEFT JOIN user_film_likes ufl ON f.id = ufl.film_id " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(ufl.film_id) DESC " +
                "LIMIT ?";
        Collection<Film> films = jdbcTemplate.query(sql, this::makeFilm, count);
        Map<Integer, Collection<Genre>> genres = genreDao.getFilmGenreMap(films);
        for (Film film : films)
            film.setGenres(new ArrayList<>(genres.getOrDefault(film.getId(), Collections.emptyList())));

        return films;
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        likeDao.addLike(filmId, userId);
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {
        likeDao.removeLike(filmId, userId);
    }

    @Override
    public Set<Integer> getLikes(Integer id) {
        return likeDao.getFilmLikes(id);
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        int filmId = rs.getInt("id");
        int ratingId = rs.getInt("rating_id");
        String mpaName = rs.getString("rating_name");
        String mpaDesc = rs.getString("rating_description");

        Rating mpa = Rating.builder()
                .id(ratingId)
                .name(mpaName)
                .description(mpaDesc)
                .build();

        return Film.builder()
                .id(filmId)
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getLong("duration"))
                .mpa(mpa)
                .build();
    }

    private List<Genre> batchUpdateGenres(Film film) {
        String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES(?, ?)";
        List<Genre> genres = film.getGenres().stream()
                .distinct()
                .collect(Collectors.toList());
        jdbcTemplate.batchUpdate(sql, genres, genres.size(),
                (ps, genre) -> {
                    ps.setInt(1, film.getId());
                    ps.setInt(2, genre.getId());
                });
        return genres;
    }
}
