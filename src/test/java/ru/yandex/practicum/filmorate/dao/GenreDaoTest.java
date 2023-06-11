package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.impl.GenreDaoImpl;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDaoTest {

    private final GenreDaoImpl genreDao;

    @Test
    void testFindGenreById() {
        Genre genre = genreDao.findById(1).get();
        assertThat(genre).hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    void testFindGenreByIncorrectId() {
        Optional<Genre> genre = genreDao.findById(0);
        assertFalse(genre.isPresent());
    }

    @Test
    void testFindAll() {
        Collection<Genre> genres = genreDao.findAll();
        assertEquals(6, genres.size());
    }
}
