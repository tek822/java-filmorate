package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Qualifier("GenreDbStorage")
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre getGenre(int id) {
        String sql = "SELECT G.GENRE_ID, G.GENRE FROM GENRES AS G WHERE G.GENRE_ID = ?";
        Genre genre = null;
        try {
            genre = jdbcTemplate
                .query(sql, (rs, rowNum) -> makeGenre(rs), id)
                .stream()
                .findAny()
                .get();
        } catch (RuntimeException e) {
            throw new GenreNotFoundException("Жанр с id " + id + " не найден.");
        }
        return genre;
    }

    @Override
    public List<Genre> getGenres() {
        String sql = "SELECT G.GENRE_ID, G.GENRE FROM GENRES AS G ORDER BY G.GENRE_ID";
        return new ArrayList<>(jdbcTemplate
                .query(sql, (rs, rowNum) -> makeGenre(rs)));
    }

    @Override
    public int size() {
        String sql = "SELECT COUNT(*) FROM GENRES";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count == null ? 0 : count;
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return new Genre(rs.getInt("GENRE_ID"), rs.getString("GENRE"));
    }
}
