package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.validators.FilmValidator;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(
            @Qualifier("FilmDbStorage") FilmStorage filmStorage,
            @Qualifier("LikeDbStorage") LikeStorage likeStorage,
            @Qualifier("UserDbStorage") UserStorage userStorage)  {
        this.filmStorage = filmStorage;
        this.likeStorage = likeStorage;
        this.userStorage = userStorage;
    }

    public List<Film> getFilms() {
        log.info("Текущее количество фильмов {}", filmStorage.size());
        return filmStorage.getFilms();
    }

    public Film getFilm(int id) {
            return filmStorage.getFilm(id);
    }

    public Film addFilm(Film film) {
        if (!FilmValidator.isValid(film)) {
            throw new ValidationException("Данные фильма не прошли валидацию");
        }
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        if (!FilmValidator.isValid(film)) {
            throw new ValidationException("Данные фильма не прошли валидацию");
        }
        return filmStorage.updateFilm(film);
    }

    public void addLike(int fid, int uid) {
        if (!userStorage.containsUser(uid)) {
            throw new UserNotFoundException("Пользователь с id : " + uid + " не найден");
        }
        if (!filmStorage.containsFilm(fid)) {
            throw new UserNotFoundException("Фильм с id : " + fid + " не найден");
        }
        likeStorage.addLike(fid, uid);
    }

    public void deleteLike(int fid, int uid) {
        if (!userStorage.containsUser(uid)) {
            throw new UserNotFoundException("Пользователь с id : " + uid + " не найден");
        }
        if (!filmStorage.containsFilm(fid)) {
            throw new UserNotFoundException("Фильм с id : " + fid + " не найден");
        }
        likeStorage.deleteLike(fid, uid);
    }

    public List<Film> getMostPopular(int amount) {
         return filmStorage.getFilms().stream()
                .sorted((f1, f2) -> Integer.compare(likeStorage.getLikes(f2.getId()).size(),
                        likeStorage.getLikes(f1.getId()).size())
                )
                 .limit(amount)
                 .collect(Collectors.toList());
    }
}
