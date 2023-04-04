package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingDbStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FilmDbStorageTest {
    private EmbeddedDatabase embeddedDatabase;
    private JdbcTemplate jdbcTemplate;
    private GenreStorage genreStorage;
    private RatingStorage ratingStorage;
    private FilmStorage filmStorage;

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
        filmStorage = new FilmDbStorage(jdbcTemplate, ratingStorage, genreStorage);
    }

    @Test
    void getAllFilmsFromEmptyDbTest() {
        List<Film> films = filmStorage.getFilms();
        assertNotNull(films, "Должен вернуться пустой список, не null");
        assertEquals(films.size(), 0, "Список должен быть пуст");
    }

    @Test
    void createSingleFilmTest() {
        Film film1 = getFilm(1);
        filmStorage.addFilm(film1);

        assertEquals(filmStorage.size(), 1, "В списке должен быть 1 фильм");

        Film film2 = filmStorage.getFilm(1);
        assertEquals(film1, film2, "Фильм в базе должен соответствовать исходному");
    }

    @Test
    void updateFilmTest() {
        Film film1 = getFilm(1);
        filmStorage.addFilm(film1);
        film1.setName("newName");
        film1.setDescription("newDescription");
        film1.setDuration(111);
        film1.setMpa(new Rating(5, ""));
        film1.setReleaseDate(LocalDate.now().minusYears(2));
        filmStorage.updateFilm(film1);
        Film newFilm = filmStorage.getFilm(1);

        assertEquals(newFilm, film1, "Поля фильма должны обновится в базе");
    }

    @Test
    void updateUnknownFilmTest() {
        filmStorage.addFilm(getFilm(1));
        assertThrows(FilmNotFoundException.class, () -> filmStorage.updateFilm(getFilm(2)),
                "В базе нет фильма с id 1, обновление невозможно");
    }

    @Test
    void getAllFilmsSingleEntryTest() {
        Film film1 = getFilm(1);
        filmStorage.addFilm(film1);
        List<Film> films = filmStorage.getFilms();

        assertEquals(films.size(), 1, "Должен вернутся список из одного фильма");
        assertEquals(film1, films.get(0), "Фильм должен совпадать с исходным");
    }

    @AfterEach
    void release() {
        embeddedDatabase.shutdown();
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
}
