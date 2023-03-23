package ru.yandex.practicum.filmorate.exception;

public class UserNotFounfException extends RuntimeException{
    public UserNotFounfException(String massage) {
        super(massage);
    }
}
