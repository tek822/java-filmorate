package ru.yandex.practicum.filmorate.exception;

public class GenreNotFoundException extends RuntimeException {

    public GenreNotFoundException() {
        super();
    }

    public GenreNotFoundException(String message) {
        super(message);
    }
}
