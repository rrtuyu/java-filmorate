package ru.yandex.practicum.filmorate.storage.like;

import java.util.Set;

public interface LikeDao {
    Set<Integer> getFilmLikes(Integer filmId);

    void addLike(Integer filmId, Integer userId);

    void removeLike(Integer filmId, Integer userId);
}
