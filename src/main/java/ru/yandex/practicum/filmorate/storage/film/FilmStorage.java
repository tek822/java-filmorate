package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);
    Film deleteFilm(int id);
    Film updateFilm(Film film);
    Film getFilm(int id);
    List<Film> getFilms();
    int size();
}
