package ru.yandex.practicum.filmorate.storage.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component("userStorageDB")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addUser(Integer id, User user) {
        Map<String, Object> values = new HashMap<>();
        //values.put("id", id);
        values.put("name", user.getName());
        values.put("email", user.getEmail());
        values.put("login", user.getLogin());
        values.put("birthday", user.getBirthday());

        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        try {
            int generatedId = insert.executeAndReturnKey(values).intValue();
            user.setId(generatedId); // костыль, надо было заставить возвращать этот метод объект юзера на прошлом тз
        } catch (RuntimeException e) {
            throw new ValidationException(String.format("Failed to add user: %s", user));
        }
    }

    @Override
    public void updateUser(Integer id, User user) {
        StringBuilder sql = new StringBuilder()
                .append("UPDATE users ")
                .append("SET email = ?, login = ?, name = ?, birthday = ?")
                .append("WHERE id = ?");

        try {
            jdbcTemplate.update(sql.toString(), user.getEmail(), user.getLogin(),
                    user.getName(), user.getBirthday(), id);
        } catch (RuntimeException e) {
            throw new ValidationException(String.format("Failed to update user: %s", user));
        }
    }

    // этот метод изменил был, т.к не вижу смысла в проверкеисключительно по id, но в тз не было указаний на этот счет
    @Override
    public boolean hasUser(Integer id) {
        String sql = "SELECT EXISTS(SELECT FROM users WHERE id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, id);
    }

    @Override
    public Optional<User> getUser(Integer id) {
        try {
            String sql = "SELECT * FROM users WHERE id = ?";
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::makeUser, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, this::makeUser);
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        StringBuilder sql = new StringBuilder()
                .append("INSERT INTO friendship_request (sender_id, receiver_id) ")
                .append("VALUES(?, ?)");
        try {
            jdbcTemplate.update(sql.toString(), userId, friendId);
        } catch (RuntimeException e) {
            throw new ValidationException(String
                    .format("Failed to add user<%d> as friend for user<%d>", friendId, userId));
        }
    }

    @Override
    public Set<Integer> getFriends(Integer id) {
        StringBuilder sql = new StringBuilder()
                .append("SELECT sender_id, receiver_id FROM friendship_request ")
                .append("WHERE sender_id = ? ");

        return new HashSet<>(jdbcTemplate.query(sql.toString(), (rs, rowNum) -> makeFriend(rs, id), id));
    }

    @Override
    public void removeFriend(Integer userId, Integer friendId) {
        StringBuilder sql = new StringBuilder()
                .append("DELETE FROM friendship_request ")
                .append("WHERE sender_id = ? AND receiver_id = ?");

        try {
            jdbcTemplate.update(sql.toString(), userId, friendId);
        } catch (RuntimeException e) {
            throw new ValidationException(String
                    .format("Failed to remove user<%d> as friend for user<%d>", friendId, userId));
        }
    }

    @Override
    public boolean hasFriend(Integer userId, Integer friendId) {
        StringBuilder sql = new StringBuilder()
                .append("SELECT EXISTS(")
                .append("SELECT FROM friendship_request ")
                .append("WHERE sender_id = ? AND receiver_id = ?)");
        return jdbcTemplate.queryForObject(sql.toString(), Boolean.class, userId, friendId);
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        int uid = rs.getInt("id");
        return User.builder()
                .id(uid)
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .friends(getFriends(uid))
                .build();
    }

    private Integer makeFriend(ResultSet rs, Integer userId) throws SQLException {
        int requestSender = rs.getInt("sender_id");
        int requestReceiver = rs.getInt("receiver_id");
        return requestReceiver == userId ? requestSender : requestReceiver;
    }
}
