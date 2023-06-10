package ru.yandex.practicum.filmorate.storage.user.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Component("userStorageRAM")
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public void addUser(Integer id, User user) {
        users.put(id, user);
    }

    @Override
    public void updateUser(Integer id, User user) {
        users.put(id, user);
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
    public Set<Integer> getFriends(Integer id) {
        return users.get(id).getFriends();
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
