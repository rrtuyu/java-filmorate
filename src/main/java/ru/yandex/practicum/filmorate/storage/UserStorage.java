package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    void addUser(Integer id, User user);

    void updateUser(Integer id, User user);

    boolean hasUser(Integer id);

    boolean hasUser(User user);

    User getUser(Integer id);

    Collection<User> getAllUsers();
}
