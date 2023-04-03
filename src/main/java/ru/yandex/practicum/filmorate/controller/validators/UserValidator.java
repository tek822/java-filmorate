package ru.yandex.practicum.filmorate.controller.validators;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;

@Slf4j
public class UserValidator {

    static public boolean isValid(User user) {
        log.info("Обрабатываются данные пользователя {}", user);
        return isEmailValid(user) && isLoginValid(user) &&
                isNameValid(user) && isBirthdayValid(user);
    }

    static boolean isEmailValid (User user) {
        // emain cannot be empty, must contain @
        String email = user.getEmail();
        boolean result = email != null && !email.isBlank() && email.contains("@") && !email.contains(" ");
        if (!result) {
            log.debug("Ошибка в поле email пользователя {}.", user);
        }
        return result;
    }

    static boolean isLoginValid (User user) {
        // login cannot be empty, must not contain spaces
        String login = user.getLogin();
        boolean result = login != null && !login.isBlank() && !login.contains(" ");
        if (!result) {
            log.debug("Ошибка в поле login пользователя {}.", user);
        }
        return result;
    }

    public static boolean isNameValid (User user) {
        String name = user.getName();
        if (name == null || name.isBlank()) {
            log.debug("Имя пользователя отсутствует, используется login {}.", user);
            user.setName(user.getLogin());
        }
        return true;
    }

    static boolean isBirthdayValid (User user) {
        // dateofbirth cannot be in future
        LocalDate birthday = user.getBirthday();
        LocalDate now = LocalDate.now();
        boolean result = birthday != null && !now.isBefore(birthday);
        if (!result) {
            log.debug("Ошибка в поле birthday пользователя {}.", user);
        }
        return result;
    }
}
