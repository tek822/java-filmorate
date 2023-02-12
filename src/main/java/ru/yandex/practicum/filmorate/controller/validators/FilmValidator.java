package ru.yandex.practicum.filmorate.controller.validators;

import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmValidator {
    static final int MAX_DESCRIPTION_LENGTH = 200;
    static final LocalDate ERA_BEGIN = LocalDate.of(1985, 12, 28);

    public static boolean isValid (Film film) {
        return isNameValid(film) && isDescriptionValid(film) &&
                isReleaseDateValid(film) && isDurationValid(film);
    }

    static boolean isNameValid(Film film) {
        String name = film.getName();
        return (name != null && !name.isBlank());
    }

    static boolean isDescriptionValid(Film film) {
        String description = film.getDescription();
        return description.length() <= MAX_DESCRIPTION_LENGTH;
    }

    static boolean isReleaseDateValid(Film film) {
        LocalDate releaseDate = film.getReleaseDate();
        return releaseDate != null && ERA_BEGIN.isBefore(releaseDate);
    }

    static boolean isDurationValid(Film film) {
        int duration = film.getDuration();
        return duration > 0;
    }
}
