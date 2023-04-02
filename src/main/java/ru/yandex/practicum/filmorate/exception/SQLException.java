package ru.yandex.practicum.filmorate.exception;

public class SQLException extends RuntimeException {

    public SQLException() {
        super();
    }

    public SQLException(String message) {
        super(message);
    }
}
