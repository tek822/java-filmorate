package ru.yandex.practicum.filmorate.storage.friend;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FriendDbStorageTest {
    private EmbeddedDatabase embeddedDatabase;
    private JdbcTemplate jdbcTemplate;
    private UserStorage userStorage;
    private FriendStorage friendStorage;

    @BeforeEach
    void init() {
        embeddedDatabase = new EmbeddedDatabaseBuilder()
                .addScript("schema.sql")
                .addScript("data.sql")
                .setType(EmbeddedDatabaseType.H2)
                .setName("testdb;MODE=PostgreSQL;DATABASE_TO_UPPER=TRUE")
                .build();
        jdbcTemplate = new JdbcTemplate(embeddedDatabase);
        userStorage = new UserDbStorage(jdbcTemplate);
        friendStorage = new FriendDbStorage(jdbcTemplate);
    }

    @Test
    void getFriendFromEmptyDbTest() {
        User user1 = getUser(1);
        User user2 = getUser(2);
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        assertEquals(friendStorage.getFriends(1).size(), 0, "У пользователя 1 не должно быть друзей");
        assertEquals(friendStorage.getFriends(2).size(), 0, "У пользователя 2 не должно быть друзей");
        assertEquals(friendStorage.getCommonFriends(1, 2).size(), 0, "У пользователей не должно быть общих друзей");
    }

    @Test
    void getFriendForUser1Test() {
        User user1 = getUser(1);
        User user2 = getUser(2);
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        friendStorage.addFriend(1, 2);
        assertEquals(userStorage.getUser(1).getFriends().size(), 1, "У пользователя 1 должен быть 1 друг");
        assertEquals(userStorage.getUser(2).getFriends().size(), 0, "У пользователя 2 не должно быть друзей");
        assertTrue(userStorage.getUser(1).getFriends().containsKey(2), "У пользователя 1 должен быть друг 2");
        assertEquals(friendStorage.getCommonFriends(1, 2).size(), 0, "У пользователtей 1 и 2 нет общих друзей");
    }

    @Test
    void getCommonFriendsFor3Test() {
        userStorage.addUser(getUser(1));
        userStorage.addUser(getUser(2));
        userStorage.addUser(getUser(3));
        friendStorage.addFriend(1, 2);
        friendStorage.addFriend(1, 3);
        friendStorage.addFriend(2, 3);

        assertEquals(userStorage.getUser(1).getFriends().size(), 2, "У пользователя 1 должно быть 2 друга");
        assertEquals(userStorage.getUser(2).getFriends().size(), 1, "У пользователя 2 должнен быть 1 друг");
        assertEquals(friendStorage.getCommonFriends(1, 2).size(), 1, "У пользователей 1,2 должен быть один общий друг");
        assertTrue(friendStorage.getCommonFriends(1, 2).contains(3), "Общий друг должен иметь id 3");
    }

    @Test
    void deleteCommonFriendTest() {
        userStorage.addUser(getUser(1));
        userStorage.addUser(getUser(2));
        userStorage.addUser(getUser(3));
        friendStorage.addFriend(1, 2);
        friendStorage.addFriend(1, 3);
        friendStorage.addFriend(2, 3);
        userStorage.deleteUser(3);

        assertEquals(friendStorage.getCommonFriends(1, 2).size(), 0, "У пользователей 1,2 нет общих друзей");
    }

    @AfterEach
    void release() {
        embeddedDatabase.shutdown();
    }

    private User getUser(int id) {
        User user = new User();
        user.setName("user" + id);
        user.setLogin("login" + id);
        user.setBirthday(LocalDate.now().minusYears(10L * id));
        user.setEmail("email" + id + "@mail.mail");
        return user;
    }
}
