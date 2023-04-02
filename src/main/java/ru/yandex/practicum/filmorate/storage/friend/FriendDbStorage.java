package ru.yandex.practicum.filmorate.storage.friend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.relational.core.sql.SQL;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.SQLException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@Qualifier("FriendDbStorage")
public class FriendDbStorage implements FriendStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(int userId, int friendId) {
        String sql = "INSERT INTO FRIENDS (USER_ID, FRIEND_ID) VALUES  (?, ?)";
        try {
            jdbcTemplate.update(sql, userId, friendId);
        } catch (RuntimeException e) {
            log.info("Ошибка добавления друга\n" +  e.getMessage());
            throw new SQLException("Ошбка добавление друга " + userId + " ," + friendId + ".\n" + e.getMessage());
        }
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        String sql = "DELETE FROM FRIENDS AS F WHERE F.USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public Set<Integer> getFriends(int id) {
        String sql = "SELECT F.FRIEND_ID FROM FRIENDS AS F WHERE F.USER_ID = ?";
        Collection<Integer> result = jdbcTemplate.query(sql, (rs, rowNumber) -> rs.getInt("FRIEND_ID"), id);
        return new HashSet<>(result);
    }

    @Override
    public Set<Integer> getCommonFriends(int user1Id, int user2id) {
        String sql = "SELECT F.FRIEND_ID FROM FRIENDS AS F WHERE USER_ID = ? AND FRIEND_ID IN " +
                "(SELECT U.FRIEND_ID FROM FRIENDS AS U WHERE USER_ID = ?)";
        Collection<Integer> result = jdbcTemplate.query(sql, (rs, rowNumber) -> rs.getInt("FRIEND_ID"), user2id, user1Id);
        return new HashSet<>(result);
    }
}
