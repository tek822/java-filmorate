package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
//import java.sql.Date;
import java.sql.SQLException;
import java.util.Collection;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("UserDbStorage")
@Primary
public class UserDbStorage implements UserStorage {
    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingColumns("EMAIL", "NAME", "LOGIN", "BIRTHDAY")
                .usingGeneratedKeyColumns("USER_ID");
        try {
            int id = simpleJdbcInsert.executeAndReturnKey(userToMap(user)).intValue();
            user.setId(id);
            log.info("Добавлен пользователь {}.", user);
        } catch (RuntimeException e) {
            log.info("Ошибка добавления пользователя UserDbStorage.addUser: " + e.getMessage());
        }
        return user;
    }

    @Override
    public User deleteUser(int id) {
        User user = getUser(id);
        if (user == null) {
            throw new UserNotFoundException("Пользователь с id: " + id + " отсутствует");
        }
        String sql = "DELETE FROM USERS WHERE USER_ID = ?";
        jdbcTemplate.update(sql, id);
        log.info("Удален пользователь {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        int id = user.getId();
        User oldUser = getUser(id);
        if (oldUser == null) {
            throw new UserNotFoundException("Пользователь с id: " + id + " отсутствует");
        }
        String sql = "UPDATE USERS SET NAME = ?, LOGIN = ?, EMAIL = ?, BIRTHDAY = ? WHERE USER_ID = ?";
        jdbcTemplate.update(sql,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                java.sql.Date.valueOf(user.getBirthday()),
                user.getId()
        );
        log.info("Обновлены данные пользователя {}", user);
        return user;
    }

    @Override
    public User getUser(int id) {
        String sql =
                "SELECT U.* " +
                "FROM USERS AS U "+
                "WHERE U.USER_ID = ?";
        User user = null;
        try {
            Collection<User> collection = jdbcTemplate.query(sql, (rs, rowNumber) -> makeUser(rs) , id);
            user = collection.stream().findAny().get();
        } catch (RuntimeException e) {
            log.info("Error getUser with id = " + id + " : " +  e.getMessage());
        }

        if (user != null) {
            user.getFriends().addAll(getFriends(id));
        } else {
            throw new UserNotFoundException("Пользователь с id: " + id + " отсутствует");
        }
        return user;
    }

    @Override
    public List<User> getUsers() {
        String sql = "SELECT USER_ID FROM USERS";
        Collection<Integer> ids = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("USER_ID"));
        return ids.stream().map(this::getUser)
                .map((user) -> {
                        user.getFriends().addAll(ids);
                        return user;
                    })
                .collect(Collectors.toList());
    }

    @Override
    public int size() {
        String sql = "SELECT COUNT(U.USER_ID) AS COUNT FROM USERS AS U GROUP BY U.USER_ID";
        Collection<Integer> count = jdbcTemplate.query(sql, (rs, rowNumber) -> getCount(rs));
        return count.size();
    }

    private Map<String, Object> userToMap(User user) {
        Map<String, Object> values = new HashMap<>();
        values.put("EMAIL", user.getEmail());
        values.put("NAME", user.getName());
        values.put("LOGIN", user.getLogin());
        values.put("BIRTHDAY", java.sql.Date.valueOf(user.getBirthday()));
        return values;
    }

    private Set<Integer> getFriends (int id) {
        String sql = "SELECT F.FRIEND_ID FROM FRIENDS AS F WHERE USER_ID = ?";
        Collection<Integer> friends = jdbcTemplate.query(sql, (rs, rowNumber) -> getFriend(rs) , id);
        return new HashSet<>(friends);
    }

    private Integer getCount(ResultSet resultSet) throws SQLException {
        return resultSet.getInt("COUNT");
    }

    private Integer getFriend(ResultSet resultSet) throws SQLException {
        return resultSet.getInt("FRIEND_ID");
    }

    private User makeUser(ResultSet resultSet) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("USER_ID"))
                .name(resultSet.getString("NAME"))
                .login(resultSet.getString("LOGIN"))
                .email(resultSet.getString("EMAIL"))
                .birthday(resultSet.getDate("BIRTHDAY").toLocalDate())
                .build();
    }
}
