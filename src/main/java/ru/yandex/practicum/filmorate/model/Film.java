package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.controller.validators.ReleaseDateConstraint;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
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
    private Rating mpa;
    private Set<Genre> genres = new HashSet<>();
/*
    final private Set<String> genres = new HashSet<>();

    public Set<String> getGenres() {
        return genres;
    }

    public void addGenre(String genre) {
        genres.add(genre);
    }

    public boolean deleteGenre(String genre) {
        return genres.remove(genre);
    }
    */

}
