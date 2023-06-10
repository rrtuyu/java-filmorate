package ru.yandex.practicum.filmorate.storage.genre.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class GenreDaoImpl implements GenreDao {
    private JdbcTemplate jdbcTemplate;

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
    public Set<Genre> getGenresOfFilm(Integer filmId) {
        StringBuilder sql = new StringBuilder()
                .append("SELECT g.id, g.name FROM film_genre AS fg ")
                .append("LEFT JOIN genre AS g ON fg.genre_id=g.id ")
                .append("WHERE fg.film_id = ?");
        return new HashSet<>(jdbcTemplate.query(sql.toString(), this::makeGenre, filmId));
    }

    @Override
    public Genre applyGenre(Integer filmId, Integer genreId) {
        String sql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId); // очищаем список жанров для фильма перед добавлением/созданием

        Map<String, Integer> values = new HashMap<>();
        values.put("film_id", filmId);
        values.put("genre_id", genreId);

        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film_genre");

        try {
            insert.execute(values);
            return findById(genreId).orElseThrow(() -> new NotFoundException("OOPS"));
        } catch (RuntimeException e) {
            throw new ValidationException(String.format("Failed to apply genre<%d> for film<%d>", genreId, filmId));
        }
    }

    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build();

    }
}
