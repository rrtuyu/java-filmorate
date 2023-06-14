package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    boolean hasFilm(Integer id);

    Optional<Film> getFilm(Integer id);

    Collection<Film> getAllFilms();

    void addLike(Integer filmId, Integer userId);

    void removeLike(Integer filmId, Integer userId);

    Set<Integer> getLikes(Integer id);
}
