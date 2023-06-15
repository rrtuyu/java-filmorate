package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("filmStorageDB") FilmStorage filmStorage,
                       @Qualifier("userStorageDB") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film createFilm(Film film) {
        log.info("Request POST /films : {}", film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(@Valid @RequestBody Film film) {
        if (!filmStorage.hasFilm(film.getId()))
            throw new NotFoundException(String.format("Film '%d' doesn't exist", film.getId()));

        log.info("Request PUT /films : {}", film);
        return filmStorage.updateFilm(film);
    }

    public Collection<Film> findAll() {
        Collection<Film> result = filmStorage.getAllFilms();
        log.info("Request GET /films : {}", result);
        return result;
    }

    public Film findById(Integer id) {
        Optional<Film> result = filmStorage.getFilm(id);
        log.info("Request GET /films/{} : {}", id, result);
        return result.orElseThrow(() -> new NotFoundException(String.format("Film %d doesn't exist", id)));
    }

    public void likeFilm(Integer id, Integer userId) {
        if (!filmStorage.hasFilm(id))
            throw new NotFoundException(String.format("Film %d doesn't exist", id));

        if (!userStorage.hasUser(userId))
            throw new NotFoundException(String.format("User %d doesn't exist", userId));

        filmStorage.addLike(id, userId);
        log.info("Request PUT /films/{}/like/{}", id, userId);
    }

    public void removeLike(Integer id, Integer userId) {
        if (!filmStorage.hasFilm(id))
            throw new NotFoundException(String.format("Film %d doesn't exist", id));

        if (!userStorage.hasUser(userId))
            throw new NotFoundException(String.format("User %d doesn't exist", userId));

        if (!filmStorage.getLikes(id).contains(userId))
            throw new NotFoundException(String.format("Film %d haven't been liked by user %d", id, userId));

        filmStorage.removeLike(id, userId);
        log.info("Request DELETE films/{}/like/{}", id, userId);
    }

    public Collection<Film> getPopular(int count) {
        log.info("Request GET /films/popular?count={}", count);
        return filmStorage.getPopular(count);
    }
}
