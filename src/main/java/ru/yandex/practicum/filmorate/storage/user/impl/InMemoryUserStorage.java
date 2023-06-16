package ru.yandex.practicum.filmorate.storage.user.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Component("userStorageRAM")
@Deprecated
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User addUser(Integer id, User user) {
        users.put(id, user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public boolean hasUser(Integer id) {
        return users.containsKey(id);
    }

    @Override
    public Optional<User> getUser(Integer id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        users.get(userId).addFriend(friendId);
    }

    @Override
    public Set<User> getFriends(Integer id) {
        return null; // Затычка)
    }

    @Override
    public void removeFriend(Integer userId, Integer friendId) {
        users.get(userId).removeFriend(friendId);
    }

    @Override
    public boolean hasFriend(Integer userId, Integer friendId) {
        return users.get(userId).getFriends().contains(friendId);
    }
}
