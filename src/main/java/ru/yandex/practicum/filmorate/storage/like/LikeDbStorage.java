package ru.yandex.practicum.filmorate.storage.like;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmorateSQLException;
import java.util.*;

@Slf4j
@Component("LikeDbStorage")
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(int fid, int uid) {
        String sql = "INSERT INTO LIKES (FILM_ID, USER_ID) VALUES  (?, ?)";
        try {
            jdbcTemplate.update(sql, fid, uid);
        } catch (RuntimeException e) {
            log.info("Ошибка добавления оценки\n{}", e.getMessage());
            throw new FilmorateSQLException("Ошибка добавление оценки " + fid + " ," + uid + ".\n" + e.getMessage());
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
            log.info("Ошибка при получении оценки фильма с id : {}", fid);
            throw new FilmorateSQLException("Ошибка при получении оценки фильма с id : " + fid + "\n" + e.getMessage());
        }
    }
}
