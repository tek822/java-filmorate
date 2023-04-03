package ru.yandex.practicum.filmorate.controller.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = {ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReleaseDateConstraintValidator.class)
public @interface ReleaseDateConstraint {
        String message() default "ReleaseDateConstraint error";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
}

