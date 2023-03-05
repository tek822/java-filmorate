package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.controller.validators.ReleaseDateConstraint;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
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
    private Set<Integer> likes = new HashSet<>();

    public Set<Integer> getLikes() {
        return likes;
    }

    public void addLike(int uid) {
        likes.add(uid);
    }

    public void deleteLike(int uid) {
        if (false == likes.remove(uid)) {
            throw new UserNotFoundException("Лайк от пользователя с id: " + uid + "не найден");
        }
    }
}
