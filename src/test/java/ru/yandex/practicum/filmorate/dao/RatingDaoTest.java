package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.rating.impl.RatingDaoImpl;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RatingDaoTest {

    private final RatingDaoImpl ratingDao;

    @Test
    void findById() {
        Rating rating = ratingDao.getById(1).get();
        assertThat(rating).hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    void findByIncorrectId() {
        Optional<Rating> rating = ratingDao.getById(6);
        assertFalse(rating.isPresent());
    }

    @Test
    void findAll() {
        Collection<Rating> ratings = ratingDao.findAll();
        assertEquals(5, ratings.size());
    }
}
