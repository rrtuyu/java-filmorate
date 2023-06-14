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
import java.util.stream.Collectors;

@Component("userStorageDB")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(Integer id, User user) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", user.getName());
        values.put("email", user.getEmail());
        values.put("login", user.getLogin());
        values.put("birthday", user.getBirthday());

        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        try {
            int generatedId = insert.executeAndReturnKey(values).intValue();
            user.setId(generatedId);
            return user;
        } catch (RuntimeException e) {
            throw new ValidationException(String.format("Failed to add user: %s", user));
        }
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE users " +
                "SET email = ?, login = ?, name = ?, birthday = ?" +
                "WHERE id = ?";

        try {
            jdbcTemplate.update(sql, user.getEmail(), user.getLogin(),
                    user.getName(), user.getBirthday(), user.getId());
            return user;
        } catch (RuntimeException e) {
            throw new ValidationException(String.format("Failed to update user: %s", user));
        }
    }

    // этот метод изменил был, т.к не вижу смысла в проверке исключительно по id, но в тз не было указаний на этот счет
    @Override
    public boolean hasUser(Integer id) {
        String sql = "SELECT EXISTS(SELECT FROM users WHERE id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, id));
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
        String sql = "INSERT INTO friendship_request (sender_id, receiver_id) " +
                "VALUES(?, ?)";
        try {
            jdbcTemplate.update(sql, userId, friendId);
        } catch (RuntimeException e) {
            throw new ValidationException(String
                    .format("Failed to add user<%d> as friend for user<%d>", friendId, userId));
        }
    }

    @Override
    public Set<User> getFriends(Integer id) {
        String sql = "WITH friend_list AS (SELECT receiver_id id " +
                "FROM friendship_request WHERE sender_id = ?) " +
                "SELECT * FROM users u " +
                "JOIN friend_list fl on u.id = fl.id";

        return new HashSet<>(jdbcTemplate.query(sql, this::makeUser, id));
    }

    @Override
    public void removeFriend(Integer userId, Integer friendId) {
        String sql = "DELETE FROM friendship_request " +
                "WHERE sender_id = ? AND receiver_id = ?";

        try {
            jdbcTemplate.update(sql, userId, friendId);
        } catch (RuntimeException e) {
            throw new ValidationException(String
                    .format("Failed to remove user<%d> as friend for user<%d>", friendId, userId));
        }
    }

    @Override
    public boolean hasFriend(Integer userId, Integer friendId) {
        String sql = "SELECT EXISTS(" +
                "SELECT FROM friendship_request " +
                "WHERE sender_id = ? AND receiver_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, userId, friendId));
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        int uid = rs.getInt("id");
        return User.builder()
                .id(uid)
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .friends(getFriends(uid).stream().map(User::getId).collect(Collectors.toSet()))
                .build();
    }
}
