package ru.yandex.practicum.filmorate.storage.user;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.data.relational.core.sql.SQL;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component("DbStorage")
public class UserDbStorage implements UserStorage {
    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        String sql = "INSERT INTO user (name, email, login, birthday) VALUES (?, ?, ?, ?)";
        jdbcTemplate.query(sql, user.getName(), user.getEmail(), user.getLogin());
        return null;
    }

    @Override
    public User deleteUser(int id) {
        return null;
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public User getUser(int id) {
        Set<Integer> set = null;
        String sql =
                "SELECT u.*, f.friend_id " +
                "FROM user as u LEFT JOIN friend AS f ON u.user_id = f.user_id " +
                "WHERE u.user_id = ?";
        Collection<User> collection = jdbcTemplate.query(sql, (rs, rowNumber) -> makeUser(rs) , id);
        User user = collection.stream().findAny().get();
        if (user != null) {
            set = collection.stream().map(User::getFriends).flatMap(Collection::stream).collect(Collectors.toSet());
        } else {
            throw new UserNotFoundException("Пользователь с id: " + id + " отсутствует");
        }
        user.getFriends().addAll(set);
        return user;
    }

    @Override
    public List<User> getUsers() {
//        String sql = "SELECT * FROM ";
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    private Set<Integer> getFriends (int id) {
        String sql = "SELECT f.friend_id FROM friends AS f WHERE user_id = ?";
        Collection<Integer> friends = jdbcTemplate.query(sql, (rs, rowNumber) -> getFriend(rs) , id);
        return new HashSet<>(friends);
    }

    private Integer getFriend(ResultSet resultSet) throws SQLException {
        return resultSet.getInt("friend_id");
    }

    private User makeUser(ResultSet resultSet) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .login(resultSet.getString("login"))
                .email(resultSet.getString("email"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }
}
