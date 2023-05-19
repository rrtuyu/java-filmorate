package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    @Autowired
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

    @PutMapping("{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") final Integer userId,
                          @PathVariable final Integer friendId) {
        service.addFriend(userId, friendId);
    }

    @GetMapping
    public Collection<User> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable final Integer id) {
        return service.findById(id);
    }

    @GetMapping("/{id}/friends")
    public Set<User> getFriendsOfUser(@PathVariable final Integer id) {
        return service.getFriendsOfUser(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> getCommonFriends(@PathVariable final Integer id,
                                      @PathVariable final Integer otherId) {
        return service.getCommonFriends(id, otherId);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public void removeFriend(@PathVariable("id") final Integer userId,
                             @PathVariable final Integer friendId) {
        service.removeFriend(userId, friendId);
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