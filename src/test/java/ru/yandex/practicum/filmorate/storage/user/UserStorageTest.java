package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public abstract class UserStorageTest<S extends UserStorage> {
    private UserStorage S;

}
