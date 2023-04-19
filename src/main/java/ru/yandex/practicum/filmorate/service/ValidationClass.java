package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Slf4j
public class ValidationClass {
    private static int MAX_LENTH_DESCRIPTION = 200;

    public static void validateFilms(Film film) {
        if (film.getName() != null) {
            if (film.getName().isBlank()) {
                log.info("Фильм не прошел валидацию. Название не может быть пустым");
                throw new ValidationException("Фильм не прошел валидацию. Название не может быть пустым");
            }
        }
        if (film.getDescription() != null) {
            if (film.getDescription().length() > MAX_LENTH_DESCRIPTION) {
                log.info("Фильм {}  не прошел валидацию. В описании более 200 символов", film.getName());
                throw new ValidationException("Фильм " + film.getName() + " не прошел валидацию. Более 200 символов");
            }
        }

        if (film.getReleaseDate() != null) {
            if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                log.info("Фильм {} не прошел валидацию.Дата создания должна быть позже 1895.12.28", film.getName());
                throw new ValidationException("Фильм " + film.getName() +
                        " не прошел валидацию.Дата создания должна быть позже 1895.12.28");
            }

        }

        if (film.getDuration() < 0) {
            log.info("Фильм {} не прошел валидацию. Продолжительность не может быть отрицательной", film.getName());
            throw new ValidationException("Фильм " + film.getName() + " не прошел валидацию. Продожительность не может быть отрицательной");
        }
    }

    public static void validateUser(User user) {
        if (user.getLogin() == null || (user.getLogin().isBlank()) || user.getLogin().contains(" ")) {
            log.info("Валидация не пройдена. Login не может быть пустым или содержать пробелы");
            throw new ValidationException("Валидация не пройдена. Login не может быть пустым или содержать пробелы");
        }
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.info("Валидация {} не пройдена. Введите корректный email", user.getEmail());
            throw new ValidationException("Валидация не пройдена. Введите корректный email");
        }


        if (user.getBirthday() != null) {
            if (user.getBirthday().isAfter(LocalDate.now())) {
                log.info("Валидация {} не пройдена. День рождения не может быть позже текущего времени", user.getBirthday());
                throw new ValidationException("Валидация не пройдена. День рождения не может быть позже текущего времени");
            }
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
