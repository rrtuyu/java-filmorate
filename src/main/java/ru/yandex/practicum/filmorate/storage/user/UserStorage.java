package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {
    User addUser(Integer id, User user);

    User updateUser(User user);

    boolean hasUser(Integer id);

    Optional<User> getUser(Integer id);

    Collection<User> getAllUsers();

    void addFriend(Integer userId, Integer friendId);

    Set<User> getFriends(Integer id);

    void removeFriend(Integer userId, Integer friendId);

    boolean hasFriend(Integer userId, Integer friendId); //не уверен, хранилищу стоит такое уметь, но почему бы и нет
}
