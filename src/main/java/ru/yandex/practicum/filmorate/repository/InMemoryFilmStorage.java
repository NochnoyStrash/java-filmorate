package ru.yandex.practicum.filmorate.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.ArrayList;
import java.util.List;

import static ru.yandex.practicum.filmorate.service.ValidationClass.validateFilms;

@Slf4j
@Component
public class InMemoryFilmStorage implements  FilmStorage  {
    private int id = 1;


    private List<Film> films = new ArrayList<>();

    public List<Film> getFilms() {
        log.debug("Пользователей в списке: {}", films.size());
        return films;
    }

    public Film addFilm(Film film) {
        validateFilms(film);

        if (films.contains(film)) {
            log.info("Фильм  уже есть в списке");
            throw new ValidationException("Фильм  уже есть в списке");
        }
        film.setId(id);
        films.add(film);
        id++;
        log.info("Фильм успешно добавлен");
        return film;
    }

    public Film updateFilm(Film film)  {
        validateFilms(film);
        if (!films.contains(film)) {
            log.info("Фильм с id {} не найден", film.getId());
            throw new FilmNotFoundException("Фильм не найден");
        }
        films.remove(film);
        films.add(film);
        return film;
    }

    public Film findFilm(Integer id) {
        return films.stream()
                .filter(film -> film.getId() == id).findFirst()
                .orElseThrow(() -> new FilmNotFoundException("Фильм с таким " + id + " не найден."));

    }

    public List<Genre> getGenres() {
        return null;
    }

    public  Genre getGenre(Integer id) {
        return null;
    }

    public List<Rating> getRatings() {
        return null;
    }

    public Rating getMPA(Integer id) {
        return null;
    }

}
