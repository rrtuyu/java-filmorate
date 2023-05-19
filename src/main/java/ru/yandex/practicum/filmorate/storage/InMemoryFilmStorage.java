package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final Map<Integer, Film> films = new HashMap<>();

    public void addFilm(Integer id, Film film) {
        films.put(id, film);
    }

    public void updateFilm(Integer id, Film film) {
        films.put(id, film);
    }

    public boolean hasFilm(Integer id) {
        return films.containsKey(id);
    }

    public boolean hasFilm(Film film) {
        return films.containsValue(film);
    }

    public Film getFilm(Integer id) {
        return films.get(id);
    }

    public Collection<Film> getAllFilms() {
        return films.values();
    }

    public void addLike(Integer filmId, Integer userId) {
        films.get(filmId).addLike(userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        films.get(filmId).removeLike(userId);
    }

    public Set<Integer> getLikes(Integer id) {
        return films.get(id).getUsersWhoLikedFilm();
    }
}
