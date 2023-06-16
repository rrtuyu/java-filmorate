package ru.yandex.practicum.filmorate.storage.genre.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class GenreDaoImpl implements GenreDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Genre> findAll() {
        String sql = "SELECT * FROM genre";
        return jdbcTemplate.query(sql, this::makeGenre);
    }

    @Override
    public Optional<Genre> findById(Integer id) {
        try {
            String sql = "SELECT * FROM genre WHERE id = ?";
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::makeGenre, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Genre> getGenresOfFilm(Integer filmId) {
        String sql = "SELECT g.id, g.name FROM film_genre AS fg " +
                "LEFT JOIN genre AS g ON fg.genre_id=g.id " +
                "WHERE fg.film_id = ?";
        return jdbcTemplate.query(sql, this::makeGenre, filmId);
    }

    @Override
    public Genre applyGenre(Integer filmId, Integer genreId) {
        Genre genre = findById(genreId)
                .orElseThrow(() -> new NotFoundException(String.format("Genre<%d> doesn't exist", genreId)));

        Map<String, Integer> values = new HashMap<>();
        values.put("film_id", filmId);
        values.put("genre_id", genre.getId());

        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film_genre");

        try {
            insert.execute(values);
            return genre;
        } catch (RuntimeException e) {
            throw new ValidationException(String.format("Failed to apply genre<%d> for film<%d>", genreId, filmId));
        }
    }

    @Override
    public void clearGenreById(Integer filmId) {
        String sql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    @Override
    public Map<Integer, Collection<Genre>> getFilmGenreMap(Collection<Film> films) {
        String inSql = films.stream()
                .map(Film::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        String sql = String.format("SELECT fg.film_id, g.id genre_id, g.name genre_name FROM film_genre fg " +
                "JOIN genre g ON g.id = fg.genre_id " +
                "WHERE fg.film_id IN (%s) " +
                "ORDER BY fg.film_id, genre_id", inSql);

        Map<Integer, Collection<Genre>> res = new HashMap<>();

        jdbcTemplate.query(sql, rs -> {
            Genre genre = Genre.builder()
                    .id(rs.getInt("genre_id"))
                    .name(rs.getString("genre_name"))
                    .build();

            Integer filmId = rs.getInt("film_id");
            res.putIfAbsent(filmId, new ArrayList<>());
            res.get(filmId).add(genre);
        });
        return res;
    }

    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build();
    }
}
