package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private int id = 1;

    private List<Film> films = new ArrayList<>();

    @GetMapping
    public List<Film> getFilms() {
        log.debug("Пользователей в списке: {}", films.size());
        return films;
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        validationFilms(film);

        if(films.contains(film)) {
            log.info("Фильм  уже есть в списке");
            throw new ValidationException("Фильм  уже есть в списке");
        }
        film.setId(id);
        films.add(film);
        id++;
        log.info("Фильм успешно добавлен");
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film)  {
        validationFilms(film);
        if(!films.contains(film)) {
            log.info("Фильм с id {} не найден", film.getId());
            throw new ValidationException("Фильм не найден");
        }
        films.remove(film);
        films.add(film);
        return film;
    }

    private void validationFilms(Film film) {
        if (film.getName() != null) {
            if(film.getName().isBlank()) {
                log.info("Фильм с названием  {} не прошел валидацию", film.getName());
                throw new ValidationException("Фильм не прошел валидацию");
            }
        }
        if(film.getDescription() != null) {
            if (film.getDescription().length() > 200) {
                log.info("Фильм с  не прошел валидацию описания");
                throw new ValidationException("Фильм не прошел валидацию");
            }
        }

        if (film.getReleaseDate() != null) {
            if (film.getReleaseDate().isBefore(LocalDate.of(1895,12,28))) {
                log.info("Фильм не прошел валидацию даты создания");
                throw new ValidationException("Фильм не прошел валидацию");
            }

        }

        if (film.getDuration() < 0) {
            log.info("Фильм не прошел валидацию продолжительности");
            throw new ValidationException("Фильм не прошел валидацию");
        }
    }

}
