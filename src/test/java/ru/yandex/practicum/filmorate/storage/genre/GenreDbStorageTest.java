package ru.yandex.practicum.filmorate.storage.genre;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingDbStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest//(exclude = DataSourceAutoConfiguration.class)
//@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class GenreDbStorageTest {
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
    void getGenreWithId1() {
        Genre genre = genreStorage.getGenre(1);

        assertNotNull(genre, "genreStorage не должен возвращать null");
        assertEquals(genre.getId(), 1, "genreStorage должен вернуть genre с id = 1");
        assertEquals(genre.getName(), "Комедия", "genreStorage должен вернуть genre с name = \"Комедия\"");
    }

    @Test
    void getGenreWithUnknownId999() {
        assertThrows(GenreNotFoundException.class, () -> genreStorage.getGenre(999), "genreStorage должен вергуть null");
    }

    @Test
    void getAllGenresTest() {
        assertDoesNotThrow(() -> (genreStorage.getGenres()), "Получение списка не должно вызывать исключений");
        List<Genre> genres = genreStorage.getGenres();
        assertEquals(genres.size(), 6, "В списке должно быть 6 жанров");
    }

    @Test
    void addSingleFilmWithGenreId1Test() {
        Film film1 = getFilm(1);
        filmStorage.addFilm(film1);

        assertEquals(film1.getGenres().size(), 1,
                "Размер списка жанров не должен измениться");
        assertEquals(film1.getGenres().toArray(new Genre[0])[0].getId(), 1,
                "id Жанра не должен измениться");
        assertEquals(film1.getGenres().toArray(new Genre[0])[0].getName(), "Комедия",
                "Список жанров должен быть обновлен правильной строкой");
    }

    @Test
    void updateSingleFilmGenreTest() {
        Film film1 = getFilm(1);
        filmStorage.addFilm(film1);
        film1.getGenres().clear();
        film1.getGenres().add(new Genre(2, ""));
        filmStorage.updateFilm(film1);

        assertEquals(film1.getGenres().size(), 1,
                "Размер списка жанров не должен измениться");
        assertEquals(film1.getGenres().toArray(new Genre[0])[0].getId(), 2,
                "id Жанра должен измениться");
        assertEquals(film1.getGenres().toArray(new Genre[0])[0].getName(), "Драма",
                "Список жанров должен быть обновлен правильной строкой");
    }

    @Test
    void updateRemoveGenresSingleFilmTest() {
        Film film1 = getFilm(1);
        filmStorage.addFilm(film1);
        film1.getGenres().clear();
        filmStorage.updateFilm(film1);

        assertEquals(film1.getGenres().size(), 0,
                "Размер списка жанров должен измениться");
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
