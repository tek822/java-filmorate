package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;
import java.util.List;

public interface GenreStorage {

    public Genre getGenre(int id);

    public List<Genre> getGenres();

    public int size();
}
