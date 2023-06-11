package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.impl.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {

    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private Film film1;
    private Film film2;
    private User user;

    @BeforeEach
    void setupFilms() {
        film1 = Film.builder()
                .id(1)
                .name("test1")
                .description("test description 1")
                .releaseDate(LocalDate.of(2010, 12, 12))
                .duration(100)
                .mpa(new Rating(1, "G", "GENERAL AUDIENCES. ALL AGES ADMITTED."))
                .build();
        film2 = Film.builder()
                .id(2)
                .name("test2")
                .description("test description 2")
                .releaseDate(LocalDate.of(2010, 12, 12))
                .duration(100)
                .mpa(new Rating(1, "G", "GENERAL AUDIENCES. ALL AGES ADMITTED."))
                .build();
        user = User.builder()
                .id(1)
                .name("test")
                .email("test@te.st")
                .login("test")
                .birthday(LocalDate.of(2020, 12, 12)).build();
    }

    @Test
    void addFilmAndFindItById() {
        filmDbStorage.addFilm(film1.getId(), film1);
        assertEquals(film1, filmDbStorage.getFilm(film1.getId()).get());
    }

    @Test
    void updateFilm() {
        addFilmAndFindItById();
        film1.setName("update name");
        filmDbStorage.updateFilm(film1.getId(), film1);
        Film film = filmDbStorage.getFilm(film1.getId()).get();
        assertThat(film).hasFieldOrPropertyWithValue("name", film1.getName());
    }

    @Test
    void hasFilm() {
        assertFalse(filmDbStorage.hasFilm(film1.getId()));
        filmDbStorage.addFilm(film1.getId(), film1);
        assertTrue(filmDbStorage.hasFilm(film1.getId()));
    }

    @Test
    void findAll() {
        filmDbStorage.addFilm(film1.getId(), film1);
        filmDbStorage.addFilm(film2.getId(), film2);

        assertEquals(2, filmDbStorage.getAllFilms().size());
    }

    @Test
    void addLikeAndGetLikes() {
        userDbStorage.addUser(user.getId(), user);
        filmDbStorage.addFilm(film1.getId(), film1);
        filmDbStorage.addLike(film1.getId(), user.getId());
        assertEquals(1, filmDbStorage.getLikes(film1.getId()).size());
    }

    @Test
    void removeLike() {
        addLikeAndGetLikes();
        filmDbStorage.removeLike(film1.getId(), user.getId());
        assertEquals(0, filmDbStorage.getLikes(film1.getId()).size());
    }
}
