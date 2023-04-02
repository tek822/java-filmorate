package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User addUser(User user);
    User deleteUser(int id);
    User updateUser(User user);
    User getUser(int id);
    List<User> getUsers();
    boolean containsUser(int id);
    int size();
}
