package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FilmStorage;
import ru.yandex.practicum.filmorate.repository.UserStorage;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;


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
        log.info("Для фильма с ID = {} добавлен лайк от пользователся с ID = {}", filmId, userId);
        return filmStorage.findFilm(filmId);
    }

    public Film deleteLike(Integer filmId, Integer userId) {
        Film film = filmStorage.findFilm(filmId);
        userStorage.getUser(userId);
        film.getLikes().remove(userId);
        filmStorage.updateFilm(film);
        log.info("Пользователь с именем {} удален", film.getName());
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


    public Film findFilm(Integer id) {
        return filmStorage.findFilm(id);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }



    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public Genre getGenre(Integer id) {
        return  filmStorage.getGenre(id);
    }

    public List<Genre> getGenres() {
        return filmStorage.getGenres();
    }

    public Rating getMpa(Integer id) {
        return filmStorage.getMPA(id);
    }

    public List<Rating> getRatings() {
        return filmStorage.getRatings();
    }
}
