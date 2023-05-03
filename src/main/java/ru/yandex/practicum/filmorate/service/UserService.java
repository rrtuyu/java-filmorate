package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class UserService {
    private final Map<Integer, User> users = new HashMap<>();
    private int localIdCounter = 1;

    public User createUser(User user) {
        validate(user);
        if (users.containsValue(user)) {
            String msg = String.format("User '%s' already exists", user.getEmail());
            log.warn(msg);
            throw new ValidationException(HttpStatus.BAD_REQUEST, msg);
        }
        users.put(user.getId(), user);
        log.info("Request POST /users : {}", user);
        return user;
    }

    public User updateUser(User user) {
        validate(user);
        if (!users.containsKey(user.getId())) {
            String msg = String.format("User '%d' already exists", user.getId());
            log.warn(msg);
            throw new ValidationException(HttpStatus.NOT_FOUND, msg);
        }
        users.put(user.getId(), user);
        log.info("Request PUT /users : {}", user);
        return user;
    }

    public Collection<User> findAll() {
        return users.values();
    }

    private void validate(User user) {
        if (user.getLogin().contains(" ")) {
            String msg = "User argument 'login' invalid: " + user.getLogin();
            log.warn(msg);
            throw new ValidationException(HttpStatus.BAD_REQUEST, msg);
        }

        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.debug("User {} 'name' field set as login '{}' by default", user.getId(), user.getLogin());
        }

        if (user.getId() == 0) {
            user.setId(localIdCounter);
            localIdCounter++;
            log.debug("User {} 'id' field set by default in increment order", user.getId());
        }
    }
}
