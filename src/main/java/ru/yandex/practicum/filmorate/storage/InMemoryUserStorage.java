package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    public void addUser(Integer id, User user) {
        users.put(id, user);
    }

    public void updateUser(Integer id, User user) {
        users.put(id, user);
    }

    public boolean hasUser(Integer id) {
        return users.containsKey(id);
    }

    public User getUser(Integer id) {
        return users.get(id);
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public void addFriend(Integer userId, Integer friendId) {
        users.get(userId).addFriend(friendId);
    }

    public Set<Integer> getFriends(Integer id) {
        return users.get(id).getFriends();
    }

    public void removeFriend(Integer userId, Integer friendId) {
        users.get(userId).removeFriend(friendId);
    }

    public boolean hasFriend(Integer userId, Integer friendId) {
        return users.get(userId).getFriends().contains(friendId);
    }
}
