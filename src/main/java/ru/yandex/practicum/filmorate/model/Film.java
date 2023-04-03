package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.controller.validators.ReleaseDateConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;
import java.util.TreeSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private int id;
    @NotBlank(message = "название фильма - обязательное поле")
    private String name;
    @Size(max = 200, message = "описание должно быть не длиннее 200 символов")
    private String description;
    @ReleaseDateConstraint
    private LocalDate releaseDate;
    @Positive
    private int duration; //minutes
    @NotNull
    private Rating mpa;
    private final Set<Genre> genres = new TreeSet<>(Comparator.comparingInt(Genre::getId));

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Film film = (Film) o;

        if (duration != film.duration) return false;
        if (!name.equals(film.name)) return false;
        if (!Objects.equals(description, film.description)) return false;
        if (!releaseDate.equals(film.releaseDate)) return false;
        return mpa.equals(film.mpa);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + releaseDate.hashCode();
        result = 31 * result + duration;
        result = 31 * result + mpa.hashCode();
        return result;
    }
}
