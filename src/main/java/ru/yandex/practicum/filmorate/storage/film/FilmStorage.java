package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Set;

public interface FilmStorage {
    Film addFilm(Film film);
    Film deleteFilm(Film film);
    Film updateFilm(Film film);
    Film getFilm(int id);
    Set<Film> getFilms();
    int size();
}
