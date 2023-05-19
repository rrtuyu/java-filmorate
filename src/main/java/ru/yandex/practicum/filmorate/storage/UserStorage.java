package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Set;

public interface UserStorage {
    void addUser(Integer id, User user);

    void updateUser(Integer id, User user);

    boolean hasUser(Integer id);

    boolean hasUser(User user);

    User getUser(Integer id);

    Collection<User> getAllUsers();

    void addFriend(Integer userId, Integer friendId);

    Set<Integer> getFriends(Integer id);

    void removeFriend(Integer userId, Integer friendId);

    boolean hasFriend(Integer userId, Integer friendId); //не уверен, хранилищу стоит такое уметь, но почему бы и нет
}
