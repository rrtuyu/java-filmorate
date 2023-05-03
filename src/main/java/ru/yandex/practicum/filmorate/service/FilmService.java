package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class FilmService {
    private final Map<Integer, Film> films = new HashMap<>();
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private int localIdCounter = 1;

    public Film createFilm(Film film) {
        validate(film);
        if (films.containsValue(film)) {
            String msg = String.format("Film id:%s already exists", film.getId());
            log.warn(msg);
            throw new ValidationException(HttpStatus.BAD_REQUEST, msg);
        }
        films.put(film.getId(), film);
        log.info("Request POST /films : {}", film);
        return film;
    }

    public Film updateFilm(@Valid @RequestBody Film film) {
        validate(film);
        if (!films.containsKey(film.getId())) {
            String msg = String.format("User '%d' already exists", film.getId());
            log.warn(msg);
            throw new ValidationException(HttpStatus.NOT_FOUND, msg);
        }
        films.put(film.getId(), film);
        log.info("Request PUT /films : {}", film);
        return film;
    }

    public Collection<Film> findAll() {
        return films.values();
    }

    private void validate(Film film) {
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            String msg = "Film argument 'releaseDate' beyond the limit: " + MIN_RELEASE_DATE;
            log.warn(msg);
            throw new ValidationException(HttpStatus.BAD_REQUEST, msg);
        }

        if (film.getId() == 0) {
            film.setId(localIdCounter);
            localIdCounter++;
            log.debug("User {} 'id' field set by default in increment order", film.getId());
        }
    }
}
