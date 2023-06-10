package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface GenreDao {
    Optional<Genre> findById(Integer id);

    Set<Genre> getGenresOfFilm(Integer filmId);

    Collection<Genre> findAll();

    Genre applyGenre(Integer filmId, Integer genreId);
}
