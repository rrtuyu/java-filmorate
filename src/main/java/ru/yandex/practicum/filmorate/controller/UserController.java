package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        validate(user);
        return service.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        validate(user);
        return service.updateUser(user);
    }

    @GetMapping
    public Collection<User> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable final Integer id) {
        Optional<User> oUser = service.findById(id);
        if (oUser.isPresent())
            return oUser.get();
        else
            throw new NotFoundException(String.format("User %d doesn't exist", id));
    }

    private void validate(User user) {
        if (user.getLogin().contains(" ")) {
            String msg = "User argument 'login' invalid: " + user.getLogin();
            log.warn(msg);
            throw new ValidationException(msg);
        }

        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.debug("User {} 'name' field set as login '{}' by default", user.getId(), user.getLogin());
        }
    }
}