package ru.yandex.practicum.filmorate.storage.like;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import ru.yandex.practicum.filmorate.exception.FilmorateSQLException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingDbStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class LikeDbStorageTest {
    private EmbeddedDatabase embeddedDatabase;
    private JdbcTemplate jdbcTemplate;
    private UserStorage userStorage;
    private FilmStorage filmStorage;
    private RatingStorage ratingStorage;
    private GenreStorage genreStorage;
    private LikeStorage likeStorage;

    @BeforeEach
    void init() {
        embeddedDatabase = new EmbeddedDatabaseBuilder()
                .addScript("schema.sql")
                .addScript("data.sql")
                .setType(EmbeddedDatabaseType.H2)
                .setName("testdb;MODE=PostgreSQL;DATABASE_TO_UPPER=TRUE")
                .build();
        jdbcTemplate = new JdbcTemplate(embeddedDatabase);
        genreStorage = new GenreDbStorage(jdbcTemplate);
        ratingStorage = new RatingDbStorage(jdbcTemplate);
        userStorage = new UserDbStorage(jdbcTemplate);
        likeStorage = new LikeDbStorage(jdbcTemplate);
        filmStorage = new FilmDbStorage(jdbcTemplate, ratingStorage, genreStorage);
    }

    @AfterEach
    void release() {
        embeddedDatabase.shutdown();
    }

    @Test
    void addLikeUnknownUserUnknownFilmTest() {
        assertThrows(FilmorateSQLException.class, () -> likeStorage.addLike(1, 1),
                "Нельзя добавить лайк неизвестному фильму от несуществуещего пользователя");
    }

    @Test
    void addLikeKnownUserUnknownFilmTest() {
        userStorage.addUser(getUser(1));
        assertThrows(FilmorateSQLException.class, () -> likeStorage.addLike(1, 1),
                "Нельзя добавить лайк неизвестному фильму");
    }

    @Test
    void addLikeUnknownUserKnownFilmTest() {
        filmStorage.addFilm(getFilm(1));
        assertThrows(FilmorateSQLException.class, () -> likeStorage.addLike(1, 1),
                "Нельзя добавить лайк от несуществуещего пользователя");
    }

    @Test
    void addSingleLikeTest() {
        userStorage.addUser(getUser(1));
        filmStorage.addFilm(getFilm(1));
        likeStorage.addLike(1, 1);
        assertEquals(likeStorage.getLikes(1).size(), 1, "Должна быть 1 запись в базе лайков для фильма с id=1");
        assertEquals(likeStorage.getLikes(2).size(), 0, "Для неизвестного фильма должен вернуться пустой set");
    }

    @Test
    void addLikeTwiceTest() {
        userStorage.addUser(getUser(1));
        filmStorage.addFilm(getFilm(1));
        likeStorage.addLike(1, 1);
        assertThrows(FilmorateSQLException.class, () -> likeStorage.addLike(1, 1));
    }

    @Test
    void deleteLikeTest() {
        userStorage.addUser(getUser(1));
        filmStorage.addFilm(getFilm(1));
        likeStorage.addLike(1, 1);
        likeStorage.deleteLike(1, 1);
        assertEquals(likeStorage.getLikes(1).size(), 0, "Должно быть 0 записей в базе лайков для фильма с id=1");
    }

    private Film getFilm(int id) {
        Film film = new Film();
        film.setId(id);
        film.setName("film" + id);
        film.setDescription("description" + id);
        film.setReleaseDate(LocalDate.now().minusDays(id));
        film.setDuration(10 * id);
        film.setMpa(new Rating(id, ""));
        film.getGenres().add(new Genre(id, ""));
        return film;
    }

    private User getUser(int id) {
        User user = new User();
        user.setName("user" + id);
        user.setLogin("login" + id);
        user.setBirthday(LocalDate.now().minusYears(10L * id));
        user.setEmail("email" + id + "@mail.mail");
        return user;
    }
}
