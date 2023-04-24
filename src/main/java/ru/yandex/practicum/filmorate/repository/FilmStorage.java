package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

public interface FilmStorage {
    List<Film> getFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film findFilm(Integer id);

    List<Genre> getGenres();

    Genre getGenre(Integer id);

    Rating getMPA(Integer id);

    List<Rating> getRatings();

}
