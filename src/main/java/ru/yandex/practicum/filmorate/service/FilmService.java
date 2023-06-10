package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDao;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreDao genreDao;

    @Autowired
    public FilmService(@Qualifier("filmStorageDB") FilmStorage filmStorage,
                       @Qualifier("userStorageDB") UserStorage userStorage,
                       GenreDao genreDao) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreDao = genreDao;
    }

    public Film createFilm(Film film) {
        if (filmStorage.hasFilm(film.getId()))
            throw new ValidationException(String.format("Film id:%d already exists", film.getId()));

        filmStorage.addFilm(film.getId(), film);
        log.info("Request POST /films : {}", film);
        return film;
    }

    public Film updateFilm(@Valid @RequestBody Film film) {
        if (!filmStorage.hasFilm(film.getId()))
            throw new NotFoundException(String.format("User '%d' already exists", film.getId()));

        filmStorage.updateFilm(film.getId(), film);
        log.info("Request PUT /films : {}", film);
        return film;
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

    public List<Film> getPopular(int count) {
        List<Film> result = filmStorage.getAllFilms().stream()
                .sorted((f1, f2) -> f2.getUsersWhoLikedFilm().size() - f1.getUsersWhoLikedFilm().size())
                .limit(count)
                .collect(Collectors.toList());
        log.info("Request GET /films/popular?count={} : {}", count, result);
        return result;
    }
}
