package ru.yandex.practicum.filmorate.controller.validators;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmValidatorTest {
    static final int MAX_DESCRIPTION_LENGTH = 200;
    static final LocalDate ERA_BEGIN = LocalDate.of(1895, 12, 28);

    @Test
    public void isNameValidTest() {
        Film film = new Film();
        assertFalse(FilmValidator.isNameValid(film), "Название фильма не должно быть null");

        film.setName("");
        assertFalse(FilmValidator.isNameValid(film), "Название фильма не должно быть пустым");

        film.setName(" ");
        assertFalse(FilmValidator.isNameValid(film), "Название фильма не должно быть пустым");

        film.setName("Some film");
        assertTrue(FilmValidator.isNameValid(film));
    }

    @Test
    public void isDescriptionValidTest() {
        Film film = new Film();
        byte[] chars = new byte[MAX_DESCRIPTION_LENGTH + 1];
        Arrays.fill(chars, (byte) 0x40);
        String description = new String(chars);
        film.setDescription(description);
        assertFalse(FilmValidator.isDescriptionValid(film), "Описание фильма должно быть короче " +
                MAX_DESCRIPTION_LENGTH + " символов");

        film.setDescription(description.substring(1));
        assertTrue(FilmValidator.isDescriptionValid(film), "Описание фильма ровно " +
                MAX_DESCRIPTION_LENGTH + " символов");
    }

    @Test
    public void isReleaseDateValidTest() {
        Film film = new Film();
        assertFalse(FilmValidator.isReleaseDateValid(film), "Дата релиза не должна быть null");

        film.setReleaseDate(ERA_BEGIN.minusDays(1));
        assertFalse(FilmValidator.isReleaseDateValid(film), "Дата раелиза не может быть раньше " + ERA_BEGIN);
    }

    @Test
    public void isDurationValidTest() {
        Film film = new Film();
        assertFalse(FilmValidator.isDurationValid(film), "Длительность не должна быть null");

        film.setDuration(0);
        assertFalse(FilmValidator.isDurationValid(film), "Длительность не должна быть 0");

        film.setDuration(-1);
        assertFalse(FilmValidator.isDurationValid(film), "Длительность не должна быть < 0");

        film.setDuration(100);
        assertTrue(FilmValidator.isDurationValid(film));
    }
}
