package ru.yandex.practicum.filmorate.controller.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

import static ru.yandex.practicum.filmorate.controller.validators.FilmValidator.CINEMA_ERA_BEGIN;

public class ReleaseDateConstraintValidator
        implements ConstraintValidator<ReleaseDateConstraint, LocalDate> {

    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext constraintValidatorContext) {
        return releaseDate != null && CINEMA_ERA_BEGIN.isBefore(releaseDate);
    }
}
