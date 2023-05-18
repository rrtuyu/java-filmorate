package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    void addFilm(Integer id, Film film);

    void updateFilm(Integer id, Film film);

    boolean hasFilm(Integer id);

    boolean hasFilm(Film film);

    Film getFilm(Integer id);

    Collection<Film> getAllFilms();
}
