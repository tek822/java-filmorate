package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.controller.validators.UserValidator;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    @Override
    public User addUser(User user) {
        user.setId(nextId++);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь {}.", user);
        return user;
    }

    @Override
    public User deleteUser(User user) {
        int id = user.getId();
        if (users.containsKey(id)) {
            users.remove(id);
        } else {
            throw new UserNotFoundException("Пользователь с id: " + id + " отсутствует");
        }
        log.info("Удален пользователь {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        int id = user.getId();
        if (users.containsKey(id)) {
            if (!UserValidator.isValid(user)) {
                throw new ValidationException("Данные пользователя не прошли валидацию");
            }
            users.replace(id, user);
        } else {
            throw new UserNotFoundException("Пользователь с id: " + id + " отсутствует");
        }
        log.info("Обновлены данные пользователя {}", user);
        return user;
    }

    @Override
    public User getUser(int id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new UserNotFoundException("Пользователь с id: " + id + " отсутствует");
        }
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public int size() {
        return users.size();
    }
}
