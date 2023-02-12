package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.controller.validators.FilmValidator;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int nextID = 1;

    @GetMapping
    public List<Film> getFilms() {
        log.info("Текущее количество фильмов {}", films.size());
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        if (!FilmValidator.isValid(film)) {
            throw new ValidationException("Данные фильма не прошли валидацию");
        }
        film.setId(nextID++);
        films.put(film.getId(), film);
        log.info("Добавлен фильм {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        if (!FilmValidator.isValid(film)) {
            throw new ValidationException("Данные фильма не прошли валидацию");
        }
        int id = film.getId();
        if (films.containsKey(id)) {
            films.replace(id, film);
        } else {
            throw new IllegalArgumentException("Фильм с id: " + id + " отсутствует");
        }
        log.info("Обновлены данные фильма {}", film);
        return film;
    }
}
