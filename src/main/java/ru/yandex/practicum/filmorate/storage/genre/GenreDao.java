package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface GenreDao {
    Optional<Genre> findById(Integer id);

    Collection<Genre> getGenresOfFilm(Integer filmId);

    Collection<Genre> findAll();

    Genre applyGenre(Integer filmId, Integer genreId);

    void clearGenreById(Integer filmId);

    Map<Integer, Collection<Genre>> getFilmGenreMap(Collection<Film> films);
}
