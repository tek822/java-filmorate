package ru.yandex.practicum.filmorate.exception;

public class FilmorateSQLException extends RuntimeException {

    public FilmorateSQLException() {
        super();
    }

    public FilmorateSQLException(String message) {
        super(message);
    }
}
