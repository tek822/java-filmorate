package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("FilmDbStorage")
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingColumns("NAME", "DESCRIPTION", "RELEASEDATE", "DURATION", "RATING")
                .usingGeneratedKeyColumns("FILM_ID");
        try {
            int id = simpleJdbcInsert.executeAndReturnKey(filmToMap(film)).intValue();
            film.setId(id);
            log.info("Добавлен фильм {}", film);
        } catch (RuntimeException e) {
            log.info("Exception in FilmDbStorage.addFilm: " + e.getMessage());
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
        Film oldfilm = getFilm(id);
        if (oldfilm == null) {
            throw new FilmNotFoundException("Фильм с id: " + id + " отсутствует");
        }
        String sql = "UPDATE FILMS SET NAME = ?, DESCRIPTION = ?," +
                " DURATION = ?,  RELEASEDATE = ?, RATING = ? WHERE film_ID = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getRating(),
                film.getId()
        );
        log.info("Обновлены данные фильма {}", film);
        return film;
    }

    @Override
    public Film getFilm(int id) {
        String sql =
                "SELECT F.* "
                        + "FROM FILMS AS S "
                        + "WHERE F.FILM_ID = ?";
        Film film = null;
        try {
            Collection<Film> collection = jdbcTemplate.query(sql, (rs, rowNumber) -> makeFilm(rs), id);
            film = collection.stream().findAny().get();
        } catch (RuntimeException e) {
            log.info("Error getFilm with id = " + id + " : " +  e.getMessage());
        }

        if (film != null) {
            film.getGenres().addAll(getGenres(id));
        } else {
            throw new FilmNotFoundException("Фильм с id: " + id + " отсутствует");
        }
        return film;
    }

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT FILM_ID FROM FILMS";
        Collection<Integer> ids = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("FILM_ID"));
        return ids.stream()
                .map(this::getFilm)
                .collect(Collectors.toList());
    }

    @Override
    public int size() {
        String sql = "SELECT COUNT(F.FILM_ID) AS COUNT FROM FILMS AS F GROUP BY F.FILM_ID";
        Collection<Integer> count = jdbcTemplate.query(sql, (rs, rowNumber) -> rs.getInt("COUNT"));
        return count.iterator().next();
    }

    private Set<String> getGenres(int id) {
        String sql = "SELECT F.GENRE FROM FILM_GENRES AS F WHERE FILM_ID = ?";
        Collection<String> genres = jdbcTemplate.query(sql, (rs, rowNumber) -> rs.getString("GENRE"), id);
        return new HashSet<>(genres);
    }

    private Map<String, Object> filmToMap (Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("NAME", film.getName());
        values.put("DESCRIPTION", film.getDescription());
        values.put("DURATION", film.getDuration());
        values.put("RELEASEDATE", java.sql.Date.valueOf(film.getReleaseDate()));
        values.put("RATING", film.getRating());
        return values;
    }

    private Film makeFilm (ResultSet resultSet) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("FILM_ID"))
                .name(resultSet.getString("NAME"))
                .description(resultSet.getString("DESCRIPTION"))
                .duration(resultSet.getInt("DURATION"))
                .releaseDate(resultSet.getDate("RELEASEDATE").toLocalDate())
                .build();
    }
}
