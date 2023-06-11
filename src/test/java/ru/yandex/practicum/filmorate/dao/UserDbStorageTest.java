package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {
    private final UserDbStorage userDbStorage;
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    public void setupUsers() {
        user1 = User.builder()
                .id(1)
                .name("test")
                .email("test@te.st")
                .login("test")
                .birthday(LocalDate.of(2020, 12, 12)).build();
        user2 = User.builder()
                .id(2)
                .name("test2")
                .email("test2@te.st")
                .login("test2")
                .birthday(LocalDate.of(2020, 12, 12)).build();
        user3 = User.builder()
                .id(3)
                .name("test3")
                .email("test3@te.st")
                .login("test3")
                .birthday(LocalDate.of(2020, 12, 12)).build();
    }

    @Test
    void findById() {
        userDbStorage.addUser(user1.getId(), user1);
        User user = userDbStorage.getUser(1).get();
        assertEquals(user, user1);
    }

    @Test
    void findByIncorrectId() {
        Optional<User> user = userDbStorage.getUser(1);
        assertFalse(user.isPresent());
    }

    @Test
    void updateUser() {
        userDbStorage.addUser(user1.getId(), user1);
        User user = userDbStorage.getUser(1).get();
        assertEquals(user, user1);

        user.setName("update name");
        userDbStorage.updateUser(user.getId(), user);
        assertEquals(user, userDbStorage.getUser(user.getId()).get());
    }

    @Test
    void hasUser() {
        assertFalse(userDbStorage.hasUser(1));
        userDbStorage.addUser(user1.getId(), user1);
        assertTrue(userDbStorage.hasUser(1));
    }

    @Test
    void findAll() {
        userDbStorage.addUser(user1.getId(), user1);
        userDbStorage.addUser(user2.getId(), user2);
        userDbStorage.addUser(user3.getId(), user3);

        assertEquals(3, userDbStorage.getAllUsers().size());
    }

    @Test
    void addFriend() {
        userDbStorage.addUser(user1.getId(), user1);
        userDbStorage.addUser(user2.getId(), user2);

        userDbStorage.addFriend(user1.getId(), user2.getId());
        assertEquals(1, userDbStorage.getFriends(user1.getId()).size());
    }

    @Test
    void removeFriend() {
        addFriend();
        userDbStorage.removeFriend(user1.getId(), user2.getId());
        assertEquals(0, userDbStorage.getFriends(user1.getId()).size());
    }
}
