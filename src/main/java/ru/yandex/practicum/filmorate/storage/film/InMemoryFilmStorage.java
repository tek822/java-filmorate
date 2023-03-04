package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.controller.validators.FilmValidator;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int nextID = 1;

    @Override
    public Film addFilm(Film film) {
        if (!FilmValidator.isValid(film)) {
            throw new ValidationException("Данные фильма не прошли валидацию");
        }
        film.setId(nextID++);
        films.put(film.getId(), film);
        log.info("Добавлен фильм {}", film);
        return film;
    }

    @Override
    public Film deleteFilm(Film film) {
        int id = film.getId();
        if (films.containsKey(id)) {
            films.remove(id);
        } else {
            throw new IllegalArgumentException("Пользователь с id: " + id + " отсутствует");
        }
        log.info("Удален фильм {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        int id = film.getId();
        if (films.containsKey(id)) {
            if (!FilmValidator.isValid(film)) {
                throw new ValidationException("Данные фильма не прошли валидацию");
            }
            films.replace(id, film);
        } else {
            throw new IllegalArgumentException("Фильм с id: " + id + " отсутствует");
        }
        log.info("Обновлены данные фильма {}", film);
        return film;
    }

    @Override
    public Film getFilm(int id) {
        return films.get(id);
    }

    @Override
    public Set<Film> getFilms() {
        return new HashSet<Film>(films.values());
    }

    @Override
    public int size() {
        return 0;
    }
}
