package ru.yandex.practicum.filmorate.storage.rating;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.RatingNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Component
@Qualifier("RatingDbStorage")
public class RatingDbStorage implements RatingStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RatingDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Rating getRating(int id) {
        String sql = "SELECT R.RATING_ID, R.RATING FROM RATINGS AS R WHERE R.RATING_ID = ?";
        Rating rating = null;
        try {
        rating = jdbcTemplate
            .query(sql, (rs, rowNum) -> makeRating(rs), id)
            .stream()
            .findAny()
            .get();
        } catch (RuntimeException e) {
            throw new RatingNotFoundException("Рейтинг с id " + id + " не найден");
        }
        return rating;
    }

    @Override
    public List<Rating> getRatings() {
        String sql = "SELECT R.RATING_ID, R.RATING FROM RATINGS AS R ORDER BY R.RATING_ID";
        return new ArrayList<>(jdbcTemplate
                .query(sql, (rs, rowNum) -> makeRating(rs)));
    }

    @Override
    public int size() {
            String sql = "SELECT COUNT(R.RATING_ID) AS COUNT FROM RATINGS AS R";
            Collection<Boolean> count = jdbcTemplate.query(sql, (rs, rowNumber) -> true);
            return count.size();
    }

    private Rating makeRating(ResultSet rs) throws SQLException {
        return new Rating(rs.getInt("RATING_ID"), rs.getString("RATING"));
    }
}
