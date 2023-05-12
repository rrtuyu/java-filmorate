package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private User user;

    @BeforeEach
    void initUser() {
        String email = "mail@mail.mail";
        String login = "login";
        int id = 1;
        String name = "name";
        LocalDate birthday = LocalDate.of(2000, 1, 1);
        this.user = new User(email, login, id, name, birthday);
    }

    @Test
    void validUserTest() {
        Set<ConstraintViolation<User>> v = validator.validate(user);
        assertTrue(v.isEmpty());
    }

    @Test
    void shouldFailWhenEmailIsEmpty() {
        user.setEmail("");
        Set<ConstraintViolation<User>> v = validator.validate(user);
        assertEquals(1, v.size(), "Failed validation for email value is empty");
    }

    @Test
    void shouldFailWhenEmailIsBlank() {
        user.setEmail(" ");
        Set<ConstraintViolation<User>> v = validator.validate(user);
        assertEquals(1, v.size(), "Failed validation for email value is blank");
    }

    @Test
    void shouldFailWhenEmailIsNull() {
        user.setEmail(null);
        Set<ConstraintViolation<User>> v = validator.validate(user);
        assertEquals(1, v.size(), "Failed validation for email value is null");
    }

    @Test
    void shouldFailWhenEmailIsIncorrect1() {
        user.setEmail("mail");
        Set<ConstraintViolation<User>> v = validator.validate(user);
        assertEquals(1, v.size(), "Failed validation for email value mail"/* + email*/);
    }

    @Test
    void shouldFailWhenEmailIsIncorrect2() {
        user.setEmail("@mail");
        Set<ConstraintViolation<User>> v = validator.validate(user);
        assertEquals(1, v.size(), "Failed validation for email value @mail"/* + email*/);
    }

    @Test
    void shouldFailWhenEmailIsIncorrect3() {
        user.setEmail("mail@");
        Set<ConstraintViolation<User>> v = validator.validate(user);
        assertEquals(1, v.size(), "Failed validation for email value mail@"/* + email*/);
    }

    @Test
    void shouldFailWhenLoginIsEmpty() {
        user.setLogin("");
        Set<ConstraintViolation<User>> v = validator.validate(user);
        assertEquals(2, v.size(), "Failed validation for login value is empty");
    }

    @Test
    void shouldFailWhenLoginIsBlank() {
        user.setLogin(" ");
        Set<ConstraintViolation<User>> v = validator.validate(user);
        assertEquals(1, v.size(), "Failed validation for login value is blank");
    }

    @Test
    void shouldFailWhenLoginIsNull() {
        user.setLogin(null);
        Set<ConstraintViolation<User>> v = validator.validate(user);
        assertEquals(2, v.size(), "Failed validation for login value is null");
    }

    @Test
    void shouldNotFailWhenBirthdayIsPresent() {
        user.setBirthday(LocalDate.now());
        Set<ConstraintViolation<User>> v = validator.validate(user);
        assertTrue(v.isEmpty(), "Failed validation for birthday value: " + LocalDate.now());
    }

    @Test
    void shouldNotFailWhenBirthdayIsFuture() {
        user.setBirthday(LocalDate.now().plusDays(1));
        Set<ConstraintViolation<User>> v = validator.validate(user);
        assertEquals(1, v.size(), "Failed validation for birthday value: " + LocalDate.now().plusDays(1));
    }
}
