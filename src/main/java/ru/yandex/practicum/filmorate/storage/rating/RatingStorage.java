package ru.yandex.practicum.filmorate.storage.rating;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

public interface RatingStorage {

    public Rating getRating(int id);

    public List<Rating> getRatings();

    public int size();
}
