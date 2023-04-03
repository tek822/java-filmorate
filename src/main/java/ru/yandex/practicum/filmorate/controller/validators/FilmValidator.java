package ru.yandex.practicum.filmorate.controller.validators;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Slf4j
public class FilmValidator {
    static final int MAX_DESCRIPTION_LENGTH = 200;
    public static final LocalDate CINEMA_ERA_BEGIN = LocalDate.of(1895, 12, 28);

    public static boolean isValid(Film film) {
        log.info("Обрабатываются данные фильма {}", film);
        return isNameValid(film) && isDescriptionValid(film)
                && isReleaseDateValid(film) && isDurationValid(film);
    }

    static boolean isNameValid(Film film) {
        String name = film.getName();
        boolean result = name != null && !name.isBlank();
        if (!result) {
            log.debug("Ошибка в поле name фильма {}", film);
        }
        return result;
    }

    static boolean isDescriptionValid(Film film) {
        String description = film.getDescription();
        boolean result = description.length() <= MAX_DESCRIPTION_LENGTH;
        if (!result) {
            log.debug("Ошибка в поле description фильма {}", film);
        }
        return result;
    }

    static boolean isReleaseDateValid(Film film) {
        LocalDate releaseDate = film.getReleaseDate();
        boolean result = releaseDate != null && CINEMA_ERA_BEGIN.isBefore(releaseDate);
        if (!result) {
            log.debug("Ошибка в поле releaseDate фильма {}", film);
        }
        return result;
    }

    static boolean isDurationValid(Film film) {
        int duration = film.getDuration();
        boolean result = duration > 0;
        if (!result) {
            log.debug("Ошибка в поле duration фильма {}", film);
        }
        return result;
    }
}
