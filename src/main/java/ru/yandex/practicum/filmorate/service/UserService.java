package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private int localIdCounter = 1;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        checkId(user);
        if (userStorage.hasUser(user.getId())) {
            String msg = String.format("User '%s' already exists", user.getEmail());
            log.warn(msg);
            throw new ValidationException(msg);
        }
        userStorage.addUser(user.getId(), user);
        log.info("Request POST /users : {}", user);
        return user;
    }

    public User updateUser(User user) {
        checkId(user);
        if (!userStorage.hasUser(user.getId())) {
            String msg = String.format("User '%d' already exists", user.getId());
            log.warn(msg);
            throw new NotFoundException(msg);
        }
        userStorage.updateUser(user.getId(), user);
        log.info("Request PUT /users : {}", user);
        return user;
    }

    public Collection<User> findAll() {
        return userStorage.getAllUsers();
    }

    public Optional<User> findById(Integer id) {
        return Optional.ofNullable(userStorage.getUser(id));
    }

    public void checkId(User user) {
        if (user.getId() == 0) {
            user.setId(localIdCounter);
            localIdCounter++;
            log.debug("User {} 'id' field set by default in increment order", user.getId());
        }
    }
}
