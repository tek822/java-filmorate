package ru.yandex.practicum.filmorate.storage.rating;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import ru.yandex.practicum.filmorate.exception.RatingNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest//(exclude = DataSourceAutoConfiguration.class)
//@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class RatingStorageTest {
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
    void getratingWithId1Test() {
        Rating rating = ratingStorage.getRating(1);

        assertNotNull(rating, "ratingStorage не должен возвращать null");
        assertEquals(rating.getId(), 1, "ratingStorage должен вернуть rating с id = 1");
        assertEquals(rating.getName(), "G", "ratingStorage должен вернуть rating с name = \"G\"");
    }

    @Test
    void getratingWithUnknownId999Test() {
        assertThrows(RatingNotFoundException.class, () -> ratingStorage.getRating(999), "ratingStorage должен вергуть null");
    }

    @Test
    void getAllratingsTest() {
        assertDoesNotThrow(() -> (ratingStorage.getRatings()), "Получение списка не должно вызывать исключений");
        List<Rating> ratings = ratingStorage.getRatings();
        assertEquals(ratings.size(), 5, "В списке должно быть 5 рейтингов");
    }

    @Test
    void addFilmWithRatingId1Test() {
        Film film1 = getFilm(1);
        filmStorage.addFilm(film1);

        assertEquals(film1.getMpa().getId(), 1,
                "id рейтинга не должен измениться");
        assertEquals(film1.getMpa().getName(), "G",
                "Рейтинг должен быть обновлен правильной строкой");
    }

    @Test
    void updateFilmRatingTest() {
        Film film1 = getFilm(1);
        filmStorage.addFilm(film1);
        film1.setMpa(new Rating(2, ""));
        filmStorage.updateFilm(film1);

        assertEquals(film1.getMpa().getId(), 2,
                "id рейтинга должен измениться");
        assertEquals(film1.getMpa().getName(), "PG",
                "Рейтинг должен быть обновлен правильной строкой");
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
