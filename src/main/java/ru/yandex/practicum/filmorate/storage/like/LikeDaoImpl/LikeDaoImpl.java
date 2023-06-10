package ru.yandex.practicum.filmorate.storage.like.LikeDaoImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.storage.like.LikeDao;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class LikeDaoImpl implements LikeDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public LikeDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        Map<String, Integer> values = Map.of(
                "user_id", userId,
                "film_id", filmId);
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("user_film_likes");
        try {
            insert.execute(values);
        } catch (RuntimeException e) {
            throw new ValidationException(String.format("Failed to add like film<%d>, user<%d>", filmId, userId));
        }
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {
        StringBuilder sql = new StringBuilder()
                .append("DELETE FROM user_film_likes ")
                .append("WHERE film_id = ? AND user_id = ?");

        try {
            jdbcTemplate.update(sql.toString(), filmId, userId);
        } catch (RuntimeException e) {
            throw new ValidationException(String.format("Failed to remove like film<%d>, user<%d>", filmId, userId));
        }
    }

    @Override
    public Set<Integer> getFilmLikes(Integer filmId) {
        String sql = "SELECT user_id FROM user_film_likes WHERE film_id = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, filmId);
        Set<Integer> result = new HashSet<>();
        while (rows.next())
            result.add(rows.getInt("user_id"));

        return result;
    }
}
