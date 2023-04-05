package ru.yandex.practicum.filmorate.controller.validators;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class UserValidatorTest {

    @Test
    public void isEmailValidTest() {
        User user = new User();
        assertFalse(UserValidator.isEmailValid(user), "email пользователя не может быть null");

        user.setEmail("");
        assertFalse(UserValidator.isEmailValid(user), "email пользователя не может быть пуст");

        user.setEmail(" ");
        assertFalse(UserValidator.isEmailValid(user), "email пользователя не может быть пуст");

        user.setEmail("somebody.mail.com");
        assertFalse(UserValidator.isEmailValid(user), "email пользователя должен содержать @");

        user.setEmail("somobody@mail.com");
        assertTrue(UserValidator.isEmailValid(user));
    }

    @Test
    public void isLoginValidTest() {
        User user = new User();
        assertFalse(UserValidator.isLoginValid(user), "login пользователя не может быть null");

        user.setLogin("");
        assertFalse(UserValidator.isLoginValid(user), "login пользователя не может быть пуст");

        user.setLogin("smbd smth");
        assertFalse(UserValidator.isLoginValid(user), "login пользователя не может быть содуржать пробелов");

        user.setLogin("Login");
        assertTrue(UserValidator.isLoginValid(user));
    }

    @Test
    public void isNameValidTest() {
        User user = new User();
        user.setLogin("Login");

        assertTrue(UserValidator.isNameValid(user), "Имя пользователя может быть не задано");
        assertEquals(user.getLogin(), user.getName(), "Для имени исподьзуется login");

        user.setName("");
        assertTrue(UserValidator.isNameValid(user), "Имя пользователя может быть пустым");
        assertEquals(user.getLogin(), user.getName(), "Для имени исподьзуется login");
    }

    @Test
    public void isBirthdayValidTest() {
        User user = new User();
        assertFalse(UserValidator.isBirthdayValid(user), "birthday пользователя не может быть null");

        user.setBirthday(LocalDate.now().plusDays(1));
        assertFalse(UserValidator.isBirthdayValid(user), "Дата рождения не может быть в будущем");

        user.setBirthday(LocalDate.now().minusDays(1));
        assertTrue(UserValidator.isBirthdayValid(user), "Дата рождения корректна");
    }
}
