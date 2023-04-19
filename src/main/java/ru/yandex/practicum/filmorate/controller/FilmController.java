package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class FilmController {
    private FilmService service;

    @Autowired
    public FilmController(FilmService service) {
        this.service = service;
    }

    private List<Film> films = new ArrayList<>();

    @GetMapping("/films")
    public List<Film> getFilms() {
        return service.getFilmStorage().getFilms();
    }

    @PostMapping("/films")
    public Film addFilm(@RequestBody Film film) {
        service.getFilmStorage().addFilm(film);
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film film)  {
        service.getFilmStorage().updateFilm(film);
        return film;
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable Integer id) {
        return service.getFilmStorage().findFilm(id);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public Film addLike(@PathVariable Integer id, @PathVariable  Integer userId) {
        return service.addLike(id,userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Film deleteLike(@PathVariable  Integer id, @PathVariable  Integer userId) {
        return  service.deleteLike(id,userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(@RequestParam (required = false) Integer count) {
       return service.getPopularFilm(count);
    }

    @GetMapping("/genres")
    public List<Genre> getGenres() {
        return service.getFilmStorage().getGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenre(@PathVariable Integer id) {
        return service.getFilmStorage().getGenre(id);
    }

    @GetMapping("/mpa")
    public List<Rating> getRatings() {
        return service.getFilmStorage().getRatings();
    }

    @GetMapping("/mpa/{id}")
    public Rating getRating(@PathVariable Integer id) {
        return service.getFilmStorage().getMPA(id);
    }


}
