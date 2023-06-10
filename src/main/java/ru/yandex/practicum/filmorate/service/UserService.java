package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userStorageDB") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        if (userStorage.hasUser(user.getId()))
            throw new ValidationException(String.format("User '%s' already exists", user.getEmail()));

        userStorage.addUser(user.getId(), user);
        log.info("Request POST /users : {}", user);
        return user;
    }

    public User updateUser(User user) {
        if (!userStorage.hasUser(user.getId()))
            throw new NotFoundException(String.format("User '%d' doesn't exist", user.getId()));

        userStorage.updateUser(user.getId(), user);
        log.info("Request PUT /users : {}", user);
        return user;
    }

    public Collection<User> findAll() {
        Collection<User> result = userStorage.getAllUsers();
        log.info("Request GET /users : {}", result);
        return result;
    }

    public User findById(Integer id) {
        Optional<User> result = userStorage.getUser(id);
        log.info("Request GET /users/{} : {}", id, result);
        return result.orElseThrow(() -> new NotFoundException(String.format("User %d doesn't exist", id)));
    }

    public void addFriend(Integer userId, Integer friendId) {
        if (!userStorage.hasUser(userId))
            throw new NotFoundException(String.format("User %d doesn't exist", userId));

        if (!userStorage.hasUser(friendId))
            throw new NotFoundException(String.format("User %d doesn't exist", friendId));

        if (userId.equals(friendId))
            throw new ValidationException("Unable to add user with the same id as friend");

        userStorage.addFriend(userId, friendId);
        log.info("Request PUT /users/{}/friends/{}", userId, friendId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        if (!userStorage.hasUser(userId))
            throw new NotFoundException(String.format("User %d doesn't exist", userId));

        if (!userStorage.hasUser(friendId))
            throw new NotFoundException(String.format("User %d doesn't exist", friendId));

        if (!userStorage.hasFriend(userId, friendId))
            throw new NotFoundException(String.format("User %d is not in user's %d friend list", friendId, userId));

        userStorage.removeFriend(friendId, userId);
        userStorage.removeFriend(userId, friendId);
        log.info("Request DELETE /users/{}/friends/{}", userId, friendId);
    }

    public Set<User> getFriendsOfUser(Integer id) {
        if (!userStorage.hasUser(id))
            throw new NotFoundException(String.format("User %d doesn't exist", id));

        Set<Integer> friendsId = userStorage.getFriends(id);
        return friendsId.stream()
                .map(userStorage::getUser)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    public Set<User> getCommonFriends(Integer id, Integer otherId) {
        if (!userStorage.hasUser(id))
            throw new NotFoundException(String.format("User %d doesn't exist", id));

        if (!userStorage.hasUser(otherId))
            throw new NotFoundException(String.format("User %d doesn't exist", otherId));

        Set<Integer> tempFriendsSet = new HashSet<>(userStorage.getFriends(id));
        tempFriendsSet.retainAll(userStorage.getFriends(otherId));
        log.info("Request GET users/{}/friends/common/{} : {}", id, otherId, tempFriendsSet);
        return tempFriendsSet.stream()
                .map(userStorage::getUser)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }
}
