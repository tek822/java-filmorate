package ru.yandex.practicum.filmorate.controller.validators;

import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserValidator {

    static public boolean isValid(User user) {
        return isEmailValid(user) && isLoginValid(user) &&
                isNameValid(user) && isBirthdayValid(user);
    }

    static boolean isEmailValid (User user) {
        // emain cannot be empty, must contain @
        String email = user.getEmail();
        return email != null && !email.isBlank() && email.contains("@") && !email.contains(" ");
    }

    static boolean isLoginValid (User user) {
        // login cannot be empty, must not contain spaces
        String login = user.getLogin();
        return login != null && !login.isBlank() && !login.contains(" ");
    }

    static boolean isNameValid (User user) {
        String name = user.getName();
        if (name == null || name.isBlank()) {
            user.setName(user.getLogin());
        }
        return true;
    }

    static boolean isBirthdayValid (User user) {
        // dateofbirth cannot be in future
        LocalDate birthday = user.getBirthday();
        LocalDate now = LocalDate.now();
        return birthday != null && !now.isBefore(birthday);
    }
}
