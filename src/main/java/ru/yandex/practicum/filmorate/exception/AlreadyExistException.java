package ru.yandex.practicum.filmorate.exception;

public class AlreadyExistException extends RuntimeException {
    public AlreadyExistException(String s) {
        super(s);
    }

}
