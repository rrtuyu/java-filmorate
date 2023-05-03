package ru.yandex.practicum.filmorate;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private FilmService service;
    private int id;
    private String name;
    private String description;
    private LocalDate rDate;
    private long duration;


    @BeforeEach
    void init() {
        this.service = new FilmService();
        this.id = 0;
        this.name = "name";
        this.description = "description";
        this.rDate = LocalDate.of(2000, 1, 1);
        this.duration = 1L;
    }

    private Film getFilm() {
        return new Film(id, name, description, rDate, duration);
    }

    @Test
    void validFilmTest() {
        Film film = getFilm();
        Set<ConstraintViolation<Film>> v = validator.validate(film);
        assertTrue(v.isEmpty());
    }

    @Test
    void shouldFailWhenNameIsEmpty() {
        this.name = "";
        Film film = getFilm();
        Set<ConstraintViolation<Film>> v = validator.validate(film);
        assertEquals(2, v.size(), "Failed validation for name value is empty");
    }

    @Test
    void shouldFailWhenNameIsBlank() {
        this.name = " ";
        Film film = getFilm();
        Set<ConstraintViolation<Film>> v = validator.validate(film);
        assertEquals(1, v.size(), "Failed validation for name value is blank");
    }

    @Test
    void shouldFailWhenNameIsNull() {
        this.name = null;
        Film film = getFilm();
        Set<ConstraintViolation<Film>> v = validator.validate(film);
        assertEquals(2, v.size(), "Failed validation for name value is null");
    }

    @Test
    void shouldNotFailWhenDescriptionLengthIs200() {
        this.description = "A".repeat(200);
        Film film = getFilm();
        Set<ConstraintViolation<Film>> v = validator.validate(film);
        assertEquals(0, v.size(), "Failed validation for description.size() 200");
    }

    @Test
    void shouldFailWhenDescriptionLengthIs201() {
        this.description = "A".repeat(201);
        Film film = getFilm();
        Set<ConstraintViolation<Film>> v = validator.validate(film);
        assertEquals(1, v.size(), "Failed validation for description.size() 201");
    }

    @Test
    void shouldNotFailWhenReleaseIs1895_28_12() {
        this.rDate = LocalDate.of(1895, 12, 28);
        Film film = getFilm();
        assertEquals(service.createFilm(film), film);
    }

    @Test
    void shouldNotFailWhenReleaseIs1895_27_12() {
        this.rDate = LocalDate.of(1895, 12, 27);
        Film film = getFilm();
        ValidationException e = assertThrows(ValidationException.class,
                () -> service.createFilm(film));
        assertTrue(e.getMessage().contains("Film argument 'releaseDate' beyond the limit: 1895-12-28"));
    }

    @Test
    void shouldFailWhenDurationIs0() {
        this.duration = 0L;
        Film film = getFilm();
        Set<ConstraintViolation<Film>> v = validator.validate(film);
        assertEquals(1, v.size(), "Failed validation for duration value 0");
    }

    @Test
    void shouldFailWhenDurationIsNegative() {
        this.duration = -1;
        Film film = getFilm();
        Set<ConstraintViolation<Film>> v = validator.validate(film);
        assertEquals(1, v.size(), "Failed validation for duration value -1");
    }
}
