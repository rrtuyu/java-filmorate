package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.rating.RatingDao;

import java.util.Collection;
import java.util.Optional;

@Service
public class RatingService {
    private final RatingDao ratingDao;

    @Autowired
    public RatingService(RatingDao ratingDao) {
        this.ratingDao = ratingDao;
    }

    public Collection<Rating> findAll() {
        return ratingDao.findAll();
    }

    public Rating findById(Integer id) {
        return ratingDao.getById(id).orElseThrow(() -> new NotFoundException(String
                .format("Rating %d doesn't exist", id)));
    }
}
