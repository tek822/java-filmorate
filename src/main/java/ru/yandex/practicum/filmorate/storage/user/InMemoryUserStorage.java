package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("InMemeoryStorage")
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
    public User deleteUser(int id) {
        User user = users.get(id);
        if (user == null) {
            throw new UserNotFoundException("Пользователь с id: " + id + " отсутствует");
        }
        users.remove(id);
        log.info("Удален пользователь {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        int id = user.getId();
        if (!users.containsKey(id)) {
            throw new UserNotFoundException("Пользователь с id: " + id + " отсутствует");
        }
        users.replace(id, user);
        log.info("Обновлены данные пользователя {}", user);
        return user;
    }

    @Override
    public User getUser(int id) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException("Пользователь с id: " + id + " отсутствует");
        }
        return users.get(id);
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
