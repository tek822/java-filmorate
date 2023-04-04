package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.validators.UserValidator;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage,
                       @Qualifier("FriendDbStorage") FriendStorage friendStorage) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
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
        if (!userStorage.containsUser(userId)) {
            throw new UserNotFoundException("Пользователь с id : " + userId + " не найден");
        }
        if (!userStorage.containsUser(friendId)) {
            throw new UserNotFoundException("Пользователь с id : " + friendId + " не найден");
        }
        friendStorage.addFriend(userId, friendId);
        return userStorage.getUser(userId);
    }

    public User deleteFriend(int userId, int friendId) {
        if (!userStorage.containsUser(userId)) {
            throw new UserNotFoundException("Пользователь с id : " + userId + " не найден");
        }
        if (!userStorage.containsUser(friendId)) {
            throw new UserNotFoundException("Пользователь с id : " + friendId + " не найден");
        }
        friendStorage.deleteFriend(userId, friendId);
        return userStorage.getUser(userId);
    }

    public Set<User> getFriends(int id) {
        if (!userStorage.containsUser(id)) {
            throw new UserNotFoundException("Пользователь с id : " + id + " не найден");
        }
        return idsToUsers(friendStorage.getFriends(id));
    }

    public Set<User> getCommonFriends(int user1Id, int user2Id) {
        if (!userStorage.containsUser(user1Id)) {
            throw new UserNotFoundException("Пользователь с id : " + user1Id + " не найден");
        }
        if (!userStorage.containsUser(user2Id)) {
            throw new UserNotFoundException("Пользователь с id : " + user2Id + " не найден");
        }
        Set<Integer> common = friendStorage.getCommonFriends(user1Id, user2Id);
        return idsToUsers(common);
    }

    private Set<User> idsToUsers(Set<Integer> ids) {
        Set<User> set = new TreeSet<>(Comparator.comparingInt(User::getId));
        set.addAll(ids.stream()
                .map(userStorage::getUser)
                .collect(Collectors.toSet())
        );
        return set;
    }
}
