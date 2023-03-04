package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.controller.validators.UserValidator;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class UserService {
    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    public List<User> getUsers() {
        log.info("Текущее количество пользователей {}", users.size());
        return new ArrayList<>(users.values());
    }

    public User addUser(User user) {
        if (!UserValidator.isValid(user)) {
            throw new ValidationException("Данные пользователя не прошли валидацию");
        }
        UserValidator.isNameValid(user);
        user.setId(nextId++);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь {}.", user);
        return user;
    }

    public User updateUser(User user) {
        int id = user.getId();
        if (users.containsKey(id)) {
            if (!UserValidator.isValid(user)) {
                throw new ValidationException("Данные пользователя не прошли валидацию");
            }
            users.replace(id, user);
        } else {
            throw new IllegalArgumentException("Пользователь с id: " + id + " отсутствует");
        }
        log.info("Обновлены данные пользователя {}", user);
        return user;
    }
}
