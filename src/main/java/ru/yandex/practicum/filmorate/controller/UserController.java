package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.controller.validators.UserValidator;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;
    //private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public List<User> getUsers() {
        log.info("Текущее количество пользователей {}", users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        if (!UserValidator.isValid(user)) {
            throw new ValidationException("Данные пользователя не прошли валидацию");
        }
        user.setId(nextId++);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь {}.", user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        if (!UserValidator.isValid(user)) {
            throw new ValidationException("Данные пользователя не прошли валидацию");
        }
        int id = user.getId();
        if (users.containsKey(id)) {
            users.replace(id, user);
        } else {
            throw new IllegalArgumentException("Пользователь с id: " + id + " отсутствует");
        }
        log.info("Обновлены данные пользователя {}", user);
        return user;
    }
}
