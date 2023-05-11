package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class FilmService {
    private final Map<Integer, Film> films = new HashMap<>();

    private int localIdCounter = 1;

    public FilmService(){}

    public Film createFilm(Film film) {
        checkId(film);
        if (films.containsKey(film.getId())) {
            String msg = String.format("Film id:%d already exists", film.getId());
            log.warn(msg);
            throw new ValidationException(HttpStatus.BAD_REQUEST, msg);
        }
        films.put(film.getId(), film);
        log.info("Request POST /films : {}", film);
        return film;
    }

    public Film updateFilm(@Valid @RequestBody Film film) {
        checkId(film);
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

    private void checkId(Film film){
        if (film.getId() == 0) {
            film.setId(localIdCounter);
            localIdCounter++;
            log.debug("User {} 'id' field set by default in increment order", film.getId());
        }
    }
}
