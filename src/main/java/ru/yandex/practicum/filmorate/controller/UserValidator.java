package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserValidator {

    static public boolean isValid(User user) {
        return isEmailValid(user) && isLoginValid(user) &&
                isNameValid(user) && isBirthdayValid(user);
    }

    private static boolean isEmailValid (User user) {
        // emain cannot be empty, must contain @
        String email = user.getEmail();
        return email != null && !email.isBlank() && email.contains("@") && !email.contains(" ");
    }

    private static boolean isLoginValid (User user) {
        // login cannot be empty, must not contain spaces
        String login = user.getLogin();
        return login != null && !login.isBlank() && !login.contains(" ");
    }

    private static boolean isNameValid (User user) {
        String name = user.getName();
        if (name == null || name.isBlank()) {
            user.setName(user.getLogin());
        }
        return true;
    }

    private static boolean isBirthdayValid (User user) {
        // dateofbirth cannot be in future
        LocalDate birthday = user.getBirthday();
        LocalDate now = LocalDate.now();
        return birthday != null && !now.isBefore(birthday);
    }
}
