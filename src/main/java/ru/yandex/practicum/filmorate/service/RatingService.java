package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

import java.util.List;

@Slf4j
@Service
public class RatingService {
    private final RatingStorage ratingStorage;

    @Autowired
    public RatingService(RatingStorage ratingStorage) {
        this.ratingStorage = ratingStorage;
    }

    public Rating getRating(int id) {
        return ratingStorage.getRating(id);
    }

    public List<Rating> getRatings() {
        List<Rating> ratings = ratingStorage.getRatings();
        log.info("Текущее количество рейтингов {}", ratings.size());
        return ratings;
    }
}
