package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private Film film;

    @BeforeEach
    void init() {
        int id = 0;
        String name = "name";
        String description = "description";
        LocalDate rDate = LocalDate.of(2000, 1, 1);
        long duration = 1L;
        Set<Integer> usersWhoLikedFilm = new HashSet<>();
        Set<Genre> genre = new HashSet<>();
        Rating rating = new Rating(1, "G", "Test");
        film = new Film(id, name, description, rDate, duration, usersWhoLikedFilm, genre, rating);
    }

    @Test
    void validFilmTest() {
        Set<ConstraintViolation<Film>> v = validator.validate(film);
        assertTrue(v.isEmpty());
    }

    @Test
    void shouldFailWhenNameIsEmpty() {
        film.setName("");
        Set<ConstraintViolation<Film>> v = validator.validate(film);
        assertEquals(2, v.size(), "Failed validation for name value is empty");
    }

    @Test
    void shouldFailWhenNameIsBlank() {
        film.setName(" ");
        Set<ConstraintViolation<Film>> v = validator.validate(film);
        assertEquals(1, v.size(), "Failed validation for name value is blank");
    }

    @Test
    void shouldFailWhenNameIsNull() {
        film.setName(null);
        Set<ConstraintViolation<Film>> v = validator.validate(film);
        assertEquals(2, v.size(), "Failed validation for name value is null");
    }

    @Test
    void shouldNotFailWhenDescriptionLengthIs200() {
        film.setDescription("A".repeat(200));
        Set<ConstraintViolation<Film>> v = validator.validate(film);
        assertEquals(0, v.size(), "Failed validation for description.size() 200");
    }

    @Test
    void shouldFailWhenDescriptionLengthIs201() {
        film.setDescription("A".repeat(201));
        Set<ConstraintViolation<Film>> v = validator.validate(film);
        assertEquals(1, v.size(), "Failed validation for description.size() 201");
    }

    @Test
    void shouldFailWhenDurationIs0() {
        film.setDuration(0L);
        Set<ConstraintViolation<Film>> v = validator.validate(film);
        assertEquals(1, v.size(), "Failed validation for duration value 0");
    }

    @Test
    void shouldFailWhenDurationIsNegative() {
        film.setDuration(-1L);
        Set<ConstraintViolation<Film>> v = validator.validate(film);
        assertEquals(1, v.size(), "Failed validation for duration value -1");
    }
}
