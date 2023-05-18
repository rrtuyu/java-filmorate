package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private int localIdCounter = 1;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film createFilm(Film film) {
        checkId(film);
        if (filmStorage.hasFilm(film.getId())) {
            String msg = String.format("Film id:%d already exists", film.getId());
            log.warn(msg);
            throw new ValidationException(msg);
        }
        filmStorage.addFilm(film.getId(), film);
        log.info("Request POST /films : {}", film);
        return film;
    }

    public Film updateFilm(@Valid @RequestBody Film film) {
        checkId(film);
        if (!filmStorage.hasFilm(film.getId())) {
            String msg = String.format("User '%d' already exists", film.getId());
            log.warn(msg);
            throw new NotFoundException(msg);
        }
        filmStorage.updateFilm(film.getId(), film);
        log.info("Request PUT /films : {}", film);
        return film;
    }

    public Collection<Film> findAll() {
        return filmStorage.getAllFilms();
    }

    public Optional<Film> findById(Integer id) {
        return Optional.ofNullable(filmStorage.getFilm(id));
    }

    private void checkId(Film film) {
        if (film.getId() == 0) {
            film.setId(localIdCounter);
            localIdCounter++;
            log.debug("User {} 'id' field set by default in increment order", film.getId());
        }
    }
}
