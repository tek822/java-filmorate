package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
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
        film.setId(nextID++);
        films.put(film.getId(), film);
        log.info("Добавлен фильм {}", film);
        return film;
    }

    @Override
    public Film deleteFilm(int id) {
        Film film = films.get(id);
        if (!films.containsKey(id)) {
            throw new FilmNotFoundException("Фильм с id: " + id + " отсутствует");
        }
        films.remove(id);
        log.info("Удален фильм {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        int id = film.getId();
        if (!films.containsKey(id)) {
            throw new FilmNotFoundException("Фильм с id: " + id + " отсутствует");
        }
        films.replace(id, film);
        log.info("Обновлены данные фильма {}", film);
        return film;
    }

    @Override
    public Film getFilm(int id) {
        if (!films.containsKey(id)) {
            throw new FilmNotFoundException("Фильм с id: " + id + " отсутствует");
        }
        return films.get(id);
    }

    @Override
    public Set<Film> getFilms() {
        return new HashSet<Film>(films.values());
    }

    @Override
    public int size() {
        return films.size();
    }
}
