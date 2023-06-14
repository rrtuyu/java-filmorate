package ru.yandex.practicum.filmorate.storage.film.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;

@Component("fIlmStorageRAM")
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Film addFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public boolean hasFilm(Integer id) {
        return films.containsKey(id);
    }

    @Override
    public Optional<Film> getFilm(Integer id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        films.get(filmId).addLike(userId);
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {
        films.get(filmId).removeLike(userId);
    }

    @Override
    public Set<Integer> getLikes(Integer id) {
        return films.get(id).getUsersWhoLikedFilm();
    }
}
