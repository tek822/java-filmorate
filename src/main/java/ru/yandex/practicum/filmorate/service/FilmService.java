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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    @Autowired
    @Qualifier("FilmInMemoryStorage")
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
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

    public Film addLike(int filmId, int uid) {
        Film film = filmStorage.getFilm(filmId);
        film.addLike(uid);
        return film;
    }

    public Film deleteLike(int id, int userId) {
        Film film = filmStorage.getFilm(id);
        if (!film.deleteLike(userId)) {
            throw new UserNotFoundException("Лайк от пользователя с id: " + userId + "не найден");
        }
        return film;
    }

    public Set<Film> getMostPopular(int amount) {
        return filmStorage.getFilms().stream()
                .sorted( (f0, f1) -> f1.getLikes().size() - f0.getLikes().size())
                .limit(amount)
                .collect(Collectors.toSet());
    }
}
