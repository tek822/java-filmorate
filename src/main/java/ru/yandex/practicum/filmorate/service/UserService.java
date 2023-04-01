package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.validators.UserValidator;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    @Autowired
    @Qualifier("UserDbStorage")
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getUsers() {
        log.info("Текущее количество пользователей {}", userStorage.size());
        return userStorage.getUsers();
    }

    public User getUser(int id) {
        return userStorage.getUser(id);
    }

    public User addUser(User user) {
        if (!UserValidator.isValid(user)) {
            throw new ValidationException("Данные пользователя не прошли валидацию");
        }
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        if (!UserValidator.isValid(user)) {
            throw new ValidationException("Данные пользователя не прошли валидацию");
        }
        return userStorage.updateUser(user);
    }

    public User addFriend(int userId, int friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        user.addFriend(friendId);
        friend.addFriend(userId);
        return user;
    }

    public User deleteFriend(int userId, int friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        user.deleteFriend(friendId);
        friend.deleteFriend(userId);
        return user;
    }

    public Set<User> getFriends(int id) {
        Set<Integer> friends = userStorage.getUser(id).getFriends();
        return idsToUsers(friends);
    }

    public Set<User> getCommonFriends(int user1Id, int user2Id) {
        Set<Integer> friends1 = userStorage.getUser(user1Id).getFriends();
        Set<Integer> friends2 = userStorage.getUser(user2Id).getFriends();
        Set<Integer> common = friends1.stream().filter(friends2::contains).collect(Collectors.toSet());
        return idsToUsers(common);
    }

    private Set<User> idsToUsers (Set<Integer> ids) {
        Set<User> set = new TreeSet<>(Comparator.comparingInt(User::getId));
        set.addAll(ids.stream()
                .map(userStorage::getUser)
                .collect(Collectors.toSet())
        );
        return set;
    }
}
