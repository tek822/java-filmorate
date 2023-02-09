package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.exceptions.ValidationException;
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
    public List<Film> getUsers() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film addUser(@RequestBody Film film) {
        film.setId(nextID++);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film updateUser(@RequestBody Film film) {
        int id = film.getId();
        if (films.containsKey(id)) {
            films.replace(id, film);
        } else {

        }
        return film;
    }

    private void validateFilm(Film film) throws ValidationException {

    }
}
