package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
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

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        if (!UserValidator.isValid(user)) {
            throw new ValidationException("Данные пользователя не прошли валидацию");
        }
        user.setId(nextId++);
        users.put(user.getId(), user);
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
        return user;
    }
}
