package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDao;

import java.util.Collection;

@Service
public class GenreService {

    private final GenreDao genreDao;

    @Autowired
    public GenreService(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    public Collection<Genre> findAll() {
        return genreDao.findAll();
    }

    public Genre findById(Integer id) {
        return genreDao.findById(id).orElseThrow(() -> new NotFoundException(String
                .format("Genre %d doesn't exist", id)));
    }
}
