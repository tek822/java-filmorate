package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmorateSQLException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private RatingStorage ratingStorage;
    private GenreStorage genreStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate,
                         @Qualifier("RatingDbStorage") RatingStorage ratingStorage,
                         @Qualifier("GenreDbStorage") GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.ratingStorage = ratingStorage;
        this.genreStorage = genreStorage;
    }

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingColumns("NAME", "DESCRIPTION", "RELEASEDATE", "DURATION", "RATING_ID")
                .usingGeneratedKeyColumns("FILM_ID");
        try {
            int id = simpleJdbcInsert.executeAndReturnKey(filmToMap(film)).intValue();
            film.setId(id);
            log.info("Добавлен фильм {}", film);
        } catch (RuntimeException e) {
            log.info("Exception in FilmDbStorage.addFilm: {}", e.getMessage());
            throw new FilmorateSQLException(
                    "Exception in FilmDbStorage.addFilm: " + e.getMessage());
        }

        String sqlClear = "DELETE FROM FILM_GENRES WHERE FILM_ID = ?";
        String sqlInsert = "INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)";
        try {
            jdbcTemplate.update(sqlClear, film.getId());
        } catch (RuntimeException e) {
            log.info("Ошибка при очистке таблицы FILM_GENRES для FILM_ID {}", film.getId());
            throw new FilmorateSQLException(
                    "Ошибка при очистке таблицы FILM_GENRES для FILM_ID " + film.getId());
        }
        for (Genre g : film.getGenres()) {
            jdbcTemplate.update(sqlInsert, film.getId(), g.getId());
            g.setName(genreStorage.getGenre(g.getId()).getName());
        }
        if (film.getMpa() != null) {
            int mpaId = film.getMpa().getId();
            film.setMpa(ratingStorage.getRating(mpaId));
        }
        return film;
    }

    @Override
    public Film deleteFilm(int id) {
        Film film = getFilm(id);
        if (film == null) {
            throw new FilmNotFoundException("Фильм с id: " + id + " отсутствует");
        }
        String sql = "DELETE FROM FILMS WHERE FILM_ID = ?";
        jdbcTemplate.update(sql, id);
        log.info("Удален фильм {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        int id = film.getId();
        if (!containsFilm(id)) {
            throw new FilmNotFoundException("Фильм с id: " + id + " отсутствует");
        }
        String sql = "UPDATE FILMS SET NAME = ?, DESCRIPTION = ?,"
                + " DURATION = ?,  RELEASEDATE = ?, RATING_ID = ? WHERE FILM_ID = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getMpa().getId(),
                film.getId()
        );

        String sqlClear = "DELETE FROM FILM_GENRES WHERE FILM_ID = ?";
        String sqlInsert = "INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)";
        try {
            jdbcTemplate.update(sqlClear, film.getId());
        } catch (RuntimeException e) {
            log.info("Ошибка при очистке таблицы FILM_GENRES для FILM_ID {}", film.getId());
            throw new FilmorateSQLException(
                    "Ошибка при очистке таблицы FILM_GENRES для FILM_ID " + film.getId());
        }
        for (Genre g : film.getGenres()) {
            jdbcTemplate.update(sqlInsert, film.getId(), g.getId());
            g.setName(genreStorage.getGenre(g.getId()).getName());
        }

        if (film.getMpa() != null) {
            int mpaId = film.getMpa().getId();
            film.setMpa(ratingStorage.getRating(mpaId));
        }
        log.info("Обновлены данные фильма {}", film);
        return film;
    }

    @Override
    public Film getFilm(int id) {
        String sql = "SELECT * "
                    + "FROM FILMS AS F "
                    + "JOIN RATINGS AS R ON F.RATING_ID = R.RATING_ID "
                    + "WHERE FILM_ID = ?";
        Film film = null;
        try {
            Collection<Film> collection = jdbcTemplate.query(sql, (rs, rowNumber) -> makeFilm(rs), id);
            film = collection.stream().findAny().get();
        } catch (RuntimeException e) {
            log.info("Error getFilm with id = {} : {}", id, e.getMessage());
        }
        if (film == null) {
            throw new FilmNotFoundException("Фильм с id: " + id + " отсутствует");
        } else {
            film.getGenres().addAll(getGenres(id));
            return film;
        }
    }

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT * "
                + "FROM FILMS AS F "
                + "JOIN RATINGS AS R ON F.RATING_ID = R.RATING_ID ";
        List<Film> films = new ArrayList<>();

        try {
            films.addAll(jdbcTemplate.query(sql, (rs, rowNumber) -> makeFilm(rs)));
        } catch (RuntimeException e) {
            log.info("Error getFilms {}", e.getMessage());
        }

        Map<Integer, List<Genre>> genres = getAllFilmGenres();
        for (Film film : films) {
            if (genres.containsKey(film.getId())) {
                film.getGenres().addAll(genres.get(film.getId()));
            }
        }
        return films;
    }

    private Map<Integer, List<Genre>> getAllFilmGenres() {
        String sql = "SELECT * "
                + "FROM FILM_GENRES AS F "
                + "JOIN GENRES AS G ON F.GENRE_ID = G.GENRE_ID";
        Map<Integer, List<Genre>> allGenres;
        try {
            allGenres = jdbcTemplate.query(sql,
                    rs -> {
                        Map<Integer, List<Genre>> result = new HashMap<>();
                        while (rs.next()) {
                            int fid = rs.getInt("FILM_ID");
                            int gid = rs.getInt("GENRE_ID");
                            String genre = rs.getString("GENRE");

                            if (result.containsKey(fid)) {
                                result.get(fid).add(new Genre(gid, genre));
                            } else {
                                result.put(fid, new ArrayList<>());
                                result.get(fid).add(new Genre(gid, genre));
                            }
                        }
                        return result;
                    });
        } catch (RuntimeException e) {
            throw new FilmNotFoundException("Ошибка получения полного списка жанров всех фильмов.\n" + e.getMessage());
        }
        return allGenres;
    }

    @Override
    public boolean containsFilm(int id) {
        String sql = "SELECT FILM_ID FROM FILMS WHERE FILM_ID = ?";
        Collection<Integer> ids = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("FILM_ID"), id);
        return ids.size() == 1;
    }

    @Override
    public int size() {
        String sql = "SELECT COUNT(*) FROM FILMS";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count == null ? 0 : count;
    }

    private Collection<Genre> getGenres(int id) {
        Collection<Genre> genres = null;
        String sql = "SELECT F.GENRE_ID, G.GENRE "
                + "FROM FILM_GENRES AS F "
                + "JOIN GENRES AS G ON F.GENRE_ID = G.GENRE_ID "
                + "WHERE F.FILM_ID = ?";
        try {
            genres = jdbcTemplate.query(sql,
                    (rs, rowNumber) -> new Genre(rs.getInt("GENRE_ID"), rs.getString("GENRE")),
                    id);
        } catch (RuntimeException e) {
            throw new FilmorateSQLException("SQL in FilmDbStorage.getGenres");
        }
        return genres;
    }


    private Map<String, Object> filmToMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("NAME", film.getName());
        values.put("DESCRIPTION", film.getDescription());
        values.put("DURATION", film.getDuration());
        values.put("RELEASEDATE", java.sql.Date.valueOf(film.getReleaseDate()));
        values.put("RATING_ID", film.getMpa().getId());
        return values;
    }

    private Film makeFilm(ResultSet resultSet) throws SQLException {
        Film film = Film.builder()
                .id(resultSet.getInt("FILM_ID"))
                .name(resultSet.getString("NAME"))
                .description(resultSet.getString("DESCRIPTION"))
                .duration(resultSet.getInt("DURATION"))
                .releaseDate(resultSet.getDate("RELEASEDATE").toLocalDate())
                .mpa(new Rating(resultSet.getInt("RATING_ID"), resultSet.getString("RATING")))
                .build();
        return film;
    }
}
