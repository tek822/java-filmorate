package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.controller.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.controller.validators.FilmValidator;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FilmService {
    private final Map<Integer, Film> films = new HashMap<>();
    private int nextID = 1;

    public List<Film> getFilms() {
        log.info("Текущее количество фильмов {}", films.size());
        return new ArrayList<>(films.values());
    }

    public Film addFilm(Film film) {
        if (!FilmValidator.isValid(film)) {
            throw new ValidationException("Данные фильма не прошли валидацию");
        }
        film.setId(nextID++);
        films.put(film.getId(), film);
        log.info("Добавлен фильм {}", film);
        return film;
    }

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
}
