package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Set;

@Slf4j
@Component("DbStorage")
public class FilmDbStorage implements FilmStorage {
    @Override
    public Film addFilm(Film film) {
        return null;
    }

    @Override
    public Film deleteFilm(int id) {
        return null;
    }

    @Override
    public Film updateFilm(Film film) {
        return null;
    }

    @Override
    public Film getFilm(int id) {
        return null;
    }

    @Override
    public Set<Film> getFilms() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }
}
