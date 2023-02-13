package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.validators.UserValidator;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    @GetMapping
    public List<User> getUsers() {
        log.info("Текущее количество пользователей {}", users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
//        if (!UserValidator.isValid(user)) {
//            throw new ValidationException("Данные пользователя не прошли валидацию");
//        }
        UserValidator.isNameValid(user);
        user.setId(nextId++);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь {}.", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
//        if (!UserValidator.isValid(user)) {
//            throw new ValidationException("Данные пользователя не прошли валидацию");
//        }
        UserValidator.isNameValid(user);
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
