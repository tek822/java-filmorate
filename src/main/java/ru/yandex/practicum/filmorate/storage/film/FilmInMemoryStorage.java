package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;
import java.util.*;

@Slf4j
@Component
@Qualifier("FilmInMemoryStorage")
public class FilmInMemoryStorage implements FilmStorage {
    private final RatingStorage ratingStorage;
    private final GenreStorage genreStorage;
    private final Map<Integer, Film> films = new HashMap<>();
    private int nextId = 1;

    @Autowired
    public FilmInMemoryStorage(@Qualifier("RatingDbStorage") RatingStorage ratingStorage,
                               @Qualifier("GenreDbStorage")GenreStorage genreStorage) {
        this.ratingStorage = ratingStorage;
        this.genreStorage = genreStorage;
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(nextId++);
        fillMpaGenres(film);
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
        fillMpaGenres(film);
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
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public boolean containsFilm(int id) {
        return films.containsKey(id);
    }

    @Override
    public int size() {
        return films.size();
    }

    private void fillMpaGenres(Film film) {
        if (film.getMpa() != null) {
            film.setMpa(ratingStorage.getRating(film.getMpa().getId()));
        }
        for (Genre g : film.getGenres()) {
            g.setName(genreStorage.getGenre(g.getId()).getName());
        }
    }
}
