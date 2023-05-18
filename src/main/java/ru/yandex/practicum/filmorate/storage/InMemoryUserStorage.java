package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users;

    public InMemoryUserStorage() {
        this.users = new HashMap<>();
    }

    public void addUser(Integer id, User user) {
        users.put(id, user);
    }

    public void updateUser(Integer id, User user) {
        users.put(id, user);
    }

    public boolean hasUser(Integer id) {
        return users.containsKey(id);
    }

    public boolean hasUser(User user) {
        return users.containsValue(user);
    }

    public User getUser(Integer id) {
        return users.get(id);
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public Set<Integer> addFriend(Integer userId, Integer friendId) {
        users.get(userId).addFriend(friendId);
        return users.get(userId).getFriends();
    }

    public Set<Integer> getFriends(Integer id) {
        return users.get(id).getFriends();
    }

    public Set<Integer> removeFriend(Integer userId, Integer friendId) {
        users.get(userId).removeFriend(friendId);
        return users.get(userId).getFriends();
    }

    public boolean hasFriend(Integer userId, Integer friendId) {
        return users.get(userId).getFriends().contains(friendId);
    }
}
