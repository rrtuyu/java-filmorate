package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    public User findById(Integer id) {
        Optional<User> oUser = Optional.ofNullable(userStorage.getUser(id));
        if (oUser.isPresent())
            return oUser.get();
        else
            throw new NotFoundException(String.format("User %d doesn't exist", id));
    }

    public Set<Integer> addFriend(Integer userId, Integer friendId) {
        if (!userStorage.hasUser(userId))
            throw new NotFoundException(String.format("User %d doesn't exist", userId));

        if (!userStorage.hasUser(friendId))
            throw new NotFoundException(String.format("User %d doesn't exist", friendId));

        if (userId.equals(friendId))
            throw new ValidationException("Unable to add user with the same id as friend");

        return userStorage.addFriend(userId, friendId);
    }

    public Set<Integer> removeFriend(Integer userId, Integer friendId) {
        if (!userStorage.hasUser(userId))
            throw new NotFoundException(String.format("User %d doesn't exist", userId));

        if (!userStorage.hasUser(friendId))
            throw new NotFoundException(String.format("User %d doesn't exist", friendId));

        if (!userStorage.hasFriend(userId, friendId))
            throw new NotFoundException(String.format("User %d is not in user's %d friend list", friendId, userId));

        return userStorage.removeFriend(userId, friendId);
    }

    public Set<User> getFriendsOfUser(Integer id) {
        if (!userStorage.hasUser(id))
            throw new NotFoundException(String.format("User %d doesn't exist", id));

        Set<Integer> friendsId = userStorage.getFriends(id);
        return friendsId.stream().map(userStorage::getUser).collect(Collectors.toSet());
    }

    public Set<User> getCommonFriends(Integer id, Integer otherId) {
        if (!userStorage.hasUser(id))
            throw new NotFoundException(String.format("User %d doesn't exist", id));

        if (!userStorage.hasUser(otherId))
            throw new NotFoundException(String.format("User %d doesn't exist", otherId));

        Set<Integer> tempFriendsSet = new HashSet<>(userStorage.getFriends(id));
        tempFriendsSet.retainAll(userStorage.getFriends(otherId));
        return tempFriendsSet.stream().map(userStorage::getUser).collect(Collectors.toSet());
    }

    public void checkId(User user) {
        if (user.getId() == 0) {
            user.setId(localIdCounter);
            localIdCounter++;
            log.debug("User {} 'id' field set by default in increment order", user.getId());
        }
    }
}
