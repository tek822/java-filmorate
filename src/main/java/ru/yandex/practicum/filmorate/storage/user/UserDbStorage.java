package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmorateSQLException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
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
            log.info("Ошибка добавления пользователя UserDbStorage.addUser: {}", e.getMessage());
            throw new FilmorateSQLException(
                    "Ошибка добавления пользователя UserDbStorage.addUser: " + e.getMessage());
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
        if (getUser(id) == null) {
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
        User user = null;
        String sql =
                "SELECT U.* "
                + "FROM USERS AS U "
                + "WHERE U.USER_ID = ?";
        try {
            Collection<User> collection = jdbcTemplate.query(sql, (rs, rowNumber) -> makeUser(rs), id);
            user = collection.stream().findAny().get();
        } catch (RuntimeException e) {
            log.info("Error getUser with id = {} : {}", id, e.getMessage());
            throw new UserNotFoundException("Пользователь с id: " + id + " отсутствует");
        }
        Map<Integer, Boolean> friendsFromDb = getFriends(id);
        user.getFriends().putAll(friendsFromDb);
        return user;
    }

    @Override
    public boolean containsUser(int id) {
        String sql = "SELECT USER_ID FROM USERS WHERE USER_ID = ?";
        Collection<Integer> ids = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("USER_ID"), id);
        return ids.size() == 1;
    }

    @Override
    public List<User> getUsers() {
        String sql = "SELECT * FROM USERS ORDER BY USER_ID";
        List<User> users = new ArrayList<>();

        try {
            users.addAll(jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs)));
        } catch (RuntimeException e) {
            throw new FilmorateSQLException("Ошибка получения полного списка пользователей.\n" + e.getMessage());
        }

        Map<Integer, Map<Integer, Boolean>> friends = getAllFriends();
        for (User user : users) {
            if (friends.containsKey(user.getId())) {
                user.getFriends().putAll(friends.get(user.getId()));
            }
        }
        return users;
    }

    private Map<Integer, Map<Integer, Boolean>> getAllFriends() {
        String sqlFriends = "SELECT * FROM FRIENDS ORDER BY USER_ID, FRIEND_ID";
        Map<Integer, Map<Integer, Boolean>> friends;
        try {
             friends =
                    jdbcTemplate.query(sqlFriends,
                            rs -> {
                                Map<Integer, Map<Integer, Boolean>> result = new HashMap<>();
                                while (rs.next()) {
                                    int uid = rs.getInt("USER_ID");
                                    int fid = rs.getInt("FRIEND_ID");
                                    boolean status = rs.getBoolean("STATUS");
                                    if (result.containsKey(uid)) {
                                        result.get(uid).put(fid, status);
                                    } else {
                                        result.put(uid, new HashMap<>());
                                        result.get(uid).put(fid, status);
                                    }
                                }
                                return result;
                            }
                    );
        } catch (RuntimeException e) {
            throw new FilmorateSQLException("Ошибка получения полного списка друзей.\n" + e.getMessage());
        }
        return friends == null ? new HashMap<>() : friends;
    }

    @Override
    public int size() {
        String sql = "SELECT COUNT(*) FROM USERS";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count == null ? 0 : count;
    }

    private Map<String, Object> userToMap(User user) {
        Map<String, Object> values = new HashMap<>();
        values.put("EMAIL", user.getEmail());
        values.put("NAME", user.getName());
        values.put("LOGIN", user.getLogin());
        values.put("BIRTHDAY", java.sql.Date.valueOf(user.getBirthday()));
        return values;
    }

    private Map<Integer, Boolean> getFriends(int id) {
        String sql = "SELECT F.FRIEND_ID, F.STATUS FROM FRIENDS AS F WHERE F.USER_ID = ?";
        List<Map.Entry<Integer, Boolean>> col = new ArrayList<>();
        try {
            col.addAll(jdbcTemplate.query(sql, (rs, rowNumber) -> getFriend(rs), id));
        } catch (RuntimeException e) {
            throw new UserNotFoundException("Не найдены друзья пользователя с id " + id);
        }
        Map<Integer, Boolean> friends = new HashMap<>();
        for (Map.Entry e : col) {
            if (e != null) {
                int uid = (Integer) e.getKey();
                boolean value = (Boolean) e.getValue();
                friends.put(uid, value);
            }
        }
        return friends;
    }

    private Map.Entry<Integer, Boolean> getFriend(ResultSet resultSet) throws SQLException {
        log.info("resultSet : {}", resultSet.toString());
        log.info("FRIEND_ID : {}", resultSet.getInt("FRIEND_ID"));
        return Map.entry(resultSet.getInt("FRIEND_ID"), resultSet.getBoolean("STATUS"));
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
