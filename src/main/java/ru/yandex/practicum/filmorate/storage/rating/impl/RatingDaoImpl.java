package ru.yandex.practicum.filmorate.storage.rating.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.rating.RatingDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Component
public class RatingDaoImpl implements RatingDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public RatingDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Rating> findAll() {
        String sql = "SELECT * FROM rating";
        return jdbcTemplate.query(sql, this::makeRating);
    }

    @Override
    public Optional<Rating> getById(Integer id) {
        try {
            String sql = "SELECT * FROM rating WHERE id = ?";
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::makeRating, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private Rating makeRating(ResultSet rs, int rowNum) throws SQLException {
        return Rating.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .build();
    }
}
