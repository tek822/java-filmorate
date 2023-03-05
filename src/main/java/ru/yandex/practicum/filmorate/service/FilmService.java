package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Set<Film> getFilms() {
        log.info("Текущее количество фильмов {}", filmStorage.size());
        return filmStorage.getFilms();
    }

    public Film getFilm(int id) {
            return filmStorage.getFilm(id);
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film addLike(int filmId, int uid) {
        Film film = filmStorage.getFilm(filmId);
        film.addLike(uid);
        return film;
    }

    public Film deleteLike(int id, int userId) {
        Film film = filmStorage.getFilm(id);
        film.deleteLike(userId);
        return film;
    }

    public Set<Film> getMostPopular(int amount) {
        return filmStorage.getFilms().stream()
                .sorted( (f0, f1) -> f1.getLikes().size() - f0.getLikes().size())
                .limit(amount)
                .collect(Collectors.toSet());
    }
}
