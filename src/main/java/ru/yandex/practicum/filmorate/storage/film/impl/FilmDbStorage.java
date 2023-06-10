package ru.yandex.practicum.filmorate.storage.film.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDao;
import ru.yandex.practicum.filmorate.storage.like.LikeDao;
import ru.yandex.practicum.filmorate.storage.rating.RatingDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component("filmStorageDB")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDao genreDao;
    private final RatingDao ratingDao;
    private final LikeDao likeDao;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate,
                         GenreDao genreDao,
                         RatingDao ratingDao,
                         LikeDao likeDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDao = genreDao;
        this.ratingDao = ratingDao;
        this.likeDao = likeDao;
    }

    @Override
    public void addFilm(Integer id, Film film) {
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
        } catch (RuntimeException e) {
            throw new ValidationException(String.format("Failed to add film: %s", film));
        }
    }

    @Override
    public void updateFilm(Integer id, Film film) {
        StringBuilder sql = new StringBuilder()
                .append("UPDATE films ")
                .append("SET name = ?, description = ?, release_date = ?, rating_id = ?, duration = ? ")
                .append("WHERE id = ?");

        try {
            jdbcTemplate.update(sql.toString(), film.getName(), film.getDescription(), film.getReleaseDate(),
                    film.getMpa().getId(), film.getDuration(), id);
            makeGenres(film);
        } catch (RuntimeException e) {
            throw new ValidationException(String.format("Failed to update film: %s", film));
        }
    }

    @Override
    public boolean hasFilm(Integer id) {
        String sql = "SELECT EXISTS(SELECT FROM films WHERE id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, id);
    }

    @Override
    public Optional<Film> getFilm(Integer id) {
        try {
            String sql = "SELECT * FROM films WHERE id = ?";
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::makeFilm, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Film> getAllFilms() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, this::makeFilm);
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        likeDao.addLike(filmId, userId); // TODO implement
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {
        likeDao.removeLike(filmId, userId); // TODO implement
    }

    @Override
    public Set<Integer> getLikes(Integer id) {
        return likeDao.getFilmLikes(id);
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        int filmId = rs.getInt("id");
        int ratingId = rs.getInt("rating_id");
        return Film.builder()
                .id(filmId)
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .usersWhoLikedFilm(likeDao.getFilmLikes(filmId))
                .duration(rs.getLong("duration"))
                .mpa(ratingDao.getById(ratingId).get())
                .genres(genreDao.getGenresOfFilm(filmId))
                .build();
    }

    private void makeGenres(Film film) {
        Set<Genre> genres = film.getGenres();
        if (!genres.isEmpty())
            genres
                    .stream()
                    .forEach(g -> genreDao.applyGenre(film.getId(), g.getId()));
    }
}
