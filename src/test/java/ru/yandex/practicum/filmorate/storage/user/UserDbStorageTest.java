package ru.yandex.practicum.filmorate.storage.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserDbStorageTest {
    private EmbeddedDatabase embeddedDatabase;
    private JdbcTemplate jdbcTemplate;
    private UserStorage userStorage;

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
    }

    @Test
    void addGetUserTest() {
        User user = getUser(1);
        userStorage.addUser(user);

        assertEquals(user.getId(), 1, "Пользователь должен получить id = " + 1);

        User userUpdated = userStorage.getUser(1);
        assertEquals(user.getId(), userUpdated.getId(), "id пользователя должны совпадать");
        assertEquals(user.getName(), userUpdated.getName(), "name пользователя должны совпадать");
        assertEquals(user.getLogin(), userUpdated.getLogin(), "login пользователя должны совпадать");
        assertEquals(user.getEmail(), userUpdated.getEmail(), "email пользователя должны совпадать");
        assertEquals(user.getBirthday(), userUpdated.getBirthday(), "birthday пользователя должны совпадать");
    }

    @Test
    void updateNameGetUserTest() {
        String newName = "UpdatedUser1Name";
        User user = getUser(1);
        userStorage.addUser(user);
        user.setName(newName);
        userStorage.updateUser(user);
        User userUpdated = userStorage.getUser(1);

        assertEquals(user.getId(), userUpdated.getId(), "id пользователя должны совпадать");
        assertEquals(newName, userUpdated.getName(), "name пользователя должны совпадать");
        assertEquals(user.getLogin(), userUpdated.getLogin(), "login пользователя должны совпадать");
        assertEquals(user.getEmail(), userUpdated.getEmail(), "email пользователя должны совпадать");
        assertEquals(user.getBirthday(), userUpdated.getBirthday(), "birthday пользователя должны совпадать");
    }

    @Test
    void updateLoginGetUserTest() {
        String newLogin = "UpdatedUser1Login";
        User user = getUser(1);
        userStorage.addUser(user);
        user.setLogin(newLogin);
        userStorage.updateUser(user);
        User userUpdated = userStorage.getUser(1);

        assertEquals(newLogin, userUpdated.getLogin(), "login пользователя должны совпадать");
    }

    @Test
    void updateBirthdayGetUserTest() {
        LocalDate newBirthday = LocalDate.now().minusDays(10);
        User user = getUser(1);
        userStorage.addUser(user);
        user.setBirthday(newBirthday);
        userStorage.updateUser(user);
        User userUpdated = userStorage.getUser(1);

        assertEquals(newBirthday, userUpdated.getBirthday(), "birthday пользователя должны совпадать");
    }

    @Test
    void updateEmailGetUserTest() {
        String newEmail = "user1@mail.email";
        User user = getUser(1);
        userStorage.addUser(user);
        user.setEmail(newEmail);
        userStorage.updateUser(user);
        User userUpdated = userStorage.getUser(1);

        assertEquals(newEmail, userUpdated.getEmail(), "email пользователя должны совпадать");
    }

    @Test
    void getAllUsersTest() {
        User user1 = getUser(1);
        User user2 = getUser(2);
        userStorage.addUser(user1);
        userStorage.addUser(user2);

        List<User> users = userStorage.getUsers();
        assertEquals(users.size(), 2, "Должен вернуться список из 2 пользователей");
        assertEquals(users.get(0), user1, "Первый в списке должен быть пользователь с id = 1");
        assertEquals(users.get(1), user2, "Второй в списке должен быть пользователь с id = 2");
    }

    @Test
    void addAlreadyExistingUserTest() {
        User user = getUser(1);
        userStorage.addUser(user);
        userStorage.addUser(user);

        List<User> users = userStorage.getUsers();
        assertEquals(users.size(), 2, "Пользователь добавлен с уникальным id в базу еще раз");
    }

    @Test
    void containsUserTest() {
        User user = getUser(1);
        boolean doContains = userStorage.containsUser(1);
        assertFalse(doContains, "В базе не должно быть записи с id 1");
        int id = userStorage.addUser(user).getId();
        doContains = userStorage.containsUser(id);
        assertTrue(doContains, "В базе должен быть пользователь с id " + id);
    }

    @Test
    void sizeTest() {
        assertEquals(userStorage.size(), 0, "В базе не должно быть записей");
        User user = getUser(1);
        userStorage.addUser(user);
        assertEquals(userStorage.size(), 1, "В базе должна быть 1 запись");
        userStorage.deleteUser(user.getId());
        assertEquals(userStorage.size(), 0, "В базе не должно быть записей");
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
