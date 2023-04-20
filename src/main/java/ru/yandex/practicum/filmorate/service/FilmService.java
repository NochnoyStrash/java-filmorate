package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FilmStorage;
import ru.yandex.practicum.filmorate.repository.UserStorage;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {


    private FilmStorage filmStorage;
    private UserStorage userStorage;


    @Autowired
    public FilmService(FilmStorage  filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }


    public Film addLike(Integer filmId, Integer userId) {
        Film film = filmStorage.findFilm(filmId);
        User user = userStorage.getUser(userId);

        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        film.getLikes().add(user.getId());
        filmStorage.updateFilm(film);
        return filmStorage.findFilm(filmId);
    }

    public Film deleteLike(Integer filmId, Integer userId) {
        Film film = filmStorage.findFilm(filmId);
        userStorage.getUser(userId);
        film.getLikes().remove(userId);
        filmStorage.updateFilm(film);
        return filmStorage.findFilm(filmId);
    }

    public List<Film> getPopularFilm(Integer count) {
        if (count == null) {
            count = 10;
        }

        if (count <= 0) {
            throw new FilmNotFoundException("Введено отрицательно значение количества фильмов" + count);
        }
        return filmStorage.getFilms().stream()
                .sorted(Comparator.comparingInt((f) -> {
                            if (f.getLikes() == null) {
                                f.setLikes(new HashSet<>());
                            }
                            return f.getLikes().size() * -1;
                        }
                ))
                .limit(count)
                .collect(Collectors.toList());
    }


    public FilmStorage getFilmStorage() {
        return filmStorage;
    }

    public UserStorage getUserStorage() {
        return userStorage;
    }
}
