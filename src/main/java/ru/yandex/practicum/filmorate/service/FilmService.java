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
    private final static int MOST_POPULAR = 10;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Set<Film> getFilms() {
        log.info("Текущее количество фильмов {}", filmStorage.size());
        return filmStorage.getFilms();
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public void addLike(Film film, int uid) {
        filmStorage.getFilm(film.getId()).addLike(uid);
    }

    public Set<Film> getMostPopular() {
        return filmStorage.getFilms().stream()
                .sorted( (f0, f1) -> {return f1.getLikes().size() - f0.getLikes().size();})
                .limit(MOST_POPULAR)
                .collect(Collectors.toSet());
    }
}
