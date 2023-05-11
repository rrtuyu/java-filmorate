package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final FilmService service;

    public FilmController(FilmService service) {
        this.service = service;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        validate(film);
        return service.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        validate(film);
        return service.updateFilm(film);
    }

    @GetMapping
    public Collection<Film> findAll() {
        return service.findAll();
    }

    private void validate(Film film) {
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            String msg = "Film argument 'releaseDate' beyond the limit: " + MIN_RELEASE_DATE;
            log.warn(msg);
            throw new ValidationException(HttpStatus.BAD_REQUEST, msg);
        }
    }
}
