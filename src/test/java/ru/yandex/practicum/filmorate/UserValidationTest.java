package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private UserService service;
    private int id;
    private String name;
    private LocalDate birthday;
    private String email;
    private String login;


    @BeforeEach
    void init() {
        service = new UserService();
        this.email = "mail@mail.mail";
        this.login = "login";
        this.id = 1;
        this.name = "name";
        this.birthday = LocalDate.of(2000, 1, 1);
    }

    private User getUser() {
        return new User(email, login, id, name, birthday);
    }

    @Test
    void validUserTest() {
        User user = getUser();
        Set<ConstraintViolation<User>> v = validator.validate(user);
        assertTrue(v.isEmpty());
    }

    @Test
    void shouldFailWhenEmailIsEmpty() {
        this.email = "";
        User user = getUser();
        Set<ConstraintViolation<User>> v = validator.validate(user);
        assertEquals(1, v.size(), "Failed validation for email value is empty");
    }

    @Test
    void shouldFailWhenEmailIsBlank() {
        this.email = " ";
        User user = getUser();
        Set<ConstraintViolation<User>> v = validator.validate(user);
        assertEquals(1, v.size(), "Failed validation for email value is blank");
    }

    @Test
    void shouldFailWhenEmailIsNull() {
        this.email = null;
        User user = getUser();
        Set<ConstraintViolation<User>> v = validator.validate(user);
        assertEquals(1, v.size(), "Failed validation for email value is null");
    }

    @Test
    void shouldFailWhenEmailIsIncorrect1() {
        this.email = "mail";
        User user = getUser();
        Set<ConstraintViolation<User>> v = validator.validate(user);
        assertEquals(1, v.size(), "Failed validation for email value " + email);
    }

    @Test
    void shouldFailWhenEmailIsIncorrect2() {
        this.email = "@mail";
        User user = getUser();
        Set<ConstraintViolation<User>> v = validator.validate(user);
        assertEquals(1, v.size(), "Failed validation for email value " + email);
    }

    @Test
    void shouldFailWhenEmailIsIncorrect3() {
        this.email = "mail@";
        User user = getUser();
        Set<ConstraintViolation<User>> v = validator.validate(user);
        assertEquals(1, v.size(), "Failed validation for email value " + email);
    }

    @Test
    void shouldFailWhenLoginIsEmpty() {
        this.login = "";
        User user = getUser();
        Set<ConstraintViolation<User>> v = validator.validate(user);
        assertEquals(2, v.size(), "Failed validation for login value is empty");
    }

    @Test
    void shouldFailWhenLoginIsBlank() {
        this.login = " ";
        User user = getUser();
        Set<ConstraintViolation<User>> v = validator.validate(user);
        assertEquals(1, v.size(), "Failed validation for login value is blank");
    }

    @Test
    void shouldFailWhenLoginIsNull() {
        this.login = null;
        User user = getUser();
        Set<ConstraintViolation<User>> v = validator.validate(user);
        assertEquals(2, v.size(), "Failed validation for login value is null");
    }

    @Test
    void shouldFailWhenLoginHasSpace() {
        this.login = "test test";
        User user = getUser();
        ValidationException e = assertThrows(ValidationException.class,
                () -> service.createUser(user));
        assertTrue(e.getMessage().contains("User argument 'login' invalid"), "Failed validation for login value is blank");
    }

    @Test
    void shouldUseLoginAsNameWhenNameIsEmpty() {
        this.name = "";
        User user = getUser();
        service.createUser(user);
        assertEquals(user.getLogin(), user.getName());
    }

    @Test
    void shouldNotFailWhenBirthdayIsPresent() {
        this.birthday = LocalDate.now();
        User user = getUser();
        Set<ConstraintViolation<User>> v = validator.validate(user);
        assertTrue(v.isEmpty(), "Failed validation for birthday value: " + birthday);
    }

    @Test
    void shouldNotFailWhenBirthdayIsFuture() {
        this.birthday = LocalDate.now().plusDays(1l);
        User user = getUser();
        Set<ConstraintViolation<User>> v = validator.validate(user);
        assertEquals(1, v.size(), "Failed validation for birthday value: " + birthday);
    }
}
