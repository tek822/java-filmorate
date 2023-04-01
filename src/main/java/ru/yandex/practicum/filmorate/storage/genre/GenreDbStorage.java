package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("GenreDbStorage")
@Primary
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre getGenre(int id) {
        String sql = "SELECT G.GENRE_ID, G.GENRE FROM GENRES AS G WHERE G.GENRE_ID = ?";
        return jdbcTemplate
                .query(sql, (rs, rowNum) -> makeGenre(rs), id)
                .stream()
                .findAny()
                .get();
    }

    @Override
    public List<Genre> getGenres() {
        String sql = "SELECT G.GENRE_ID, G.GENRE FROM GENRES AS G";
        return jdbcTemplate
                .query(sql, (rs, rowNum) -> makeGenre(rs))
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    public int size() {
        String sql = "SELECT COUNT(G.GENRE_ID) AS COUNT FROM GENRES AS G";
        Collection<Boolean> count = jdbcTemplate.query(sql, (rs, rowNumber) -> true);
        return count.size();
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return new Genre(rs.getInt("GENRE_ID"), rs.getString("GENRE"));
    }

}
