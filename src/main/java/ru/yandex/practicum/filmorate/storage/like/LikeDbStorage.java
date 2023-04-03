package ru.yandex.practicum.filmorate.storage.like;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.SQLException;
import java.util.*;

@Slf4j
@Component("LikeDbStorage")
public class LikeDbStorage implements LikeStorage {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(int fid, int uid) {
        String sql = "INSERT INTO LIKES (FILM_ID, USER_ID) VALUES  (?, ?)";
        try {
            jdbcTemplate.update(sql, fid, uid);
        } catch (RuntimeException e) {
            log.info("Ошибка добавления оценки\n" +  e.getMessage());
            throw new SQLException("Ошбка добавление оуенки " + fid + " ," + uid + ".\n" + e.getMessage());
        }
    }

    @Override
    public void deleteLike(int fid, int uid) {
        String sql = "DELETE FROM LIKES AS L WHERE L.FILM_ID = ? AND L.USER_ID = ?";
        jdbcTemplate.update(sql, fid, uid);
    }

    @Override
    public Set<Integer> getLikes(int fid) {
        String sql = "SELECT L.USER_ID FROM LIKES AS L WHERE L.FILM_ID = ?";
        try {
            Collection<Integer> result = jdbcTemplate.query(sql, (rs, rowNumber) -> rs.getInt("USER_ID"), fid);
            return new HashSet<>(result);
        } catch (RuntimeException e) {
            log.info("Ошибка при получении оценки фильма с id : " + fid);
            throw new SQLException("Ошибка при получении оценки фильма с id : " + fid + "\n" + e.getMessage());
        }
    }

    @Override
    public Map<Integer, Integer> getMostPopular(int amount) {
        String sql = "SELECT L.FILM_ID, COUNT(L.USER_ID) as AMOUNT "
                        + "FROM LIKES AS L "
                        + "GROUP BY L.FILM_ID "
                        + "ORDER BY COUNT(L.USER_ID) DESC "
                        + "LIMIT ?";
        Map<Integer, Integer> result = new HashMap<>();
        try {
             jdbcTemplate.query(sql, (rs, rowNumber) -> Map.entry(rs.getInt("FILM_ID"), rs.getInt("AMOUNT")), amount).stream()
                     .map(e -> result.put(e.getKey(),e.getValue()));
            return result;
        } catch (RuntimeException e) {
            log.info("Ошибка при получении cписка популярных фильмов");
            throw new SQLException("Ошибка при получении cписка популярных фильмов \n" + e.getMessage());
        }
    }
}