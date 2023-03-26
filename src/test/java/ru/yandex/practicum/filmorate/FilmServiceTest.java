package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFounfException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.repository.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FilmServiceTest {
    FilmService filmService;
    Film film1;
    Film film2;
    User user1;

    @BeforeEach
    public void beforeEach() {
        filmService = new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage());
        film1 = Film.builder()
                .name("Моя прекрасная няня 1")
                .description("фильм про молодую няню")
                .releaseDate(LocalDate.of(2004,5,15))
                .duration(45)
                .build();
        filmService.getFilmStorage().addFilm(film1);

        film2 = Film.builder()
                .name("Моя прекрасная няня 2")
                .description("фильм про молодую няню")
                .releaseDate(LocalDate.of(2004,5,15))
                .duration(45)
                .build();
        filmService.getFilmStorage().addFilm(film2);
        user1 = User.builder().name("Marina")
                .email("marina@mail.ru")
                .birthday(LocalDate.of(1995,8,22))
                .login("Novak")
                .build();
        filmService.getUserStorage().createUser(user1);

    }

    @Test
    public void findFilmTest() {
        assertEquals(filmService.getFilmStorage().findFilm(film1.getId()), film1,"фильм не найден");
        final FilmNotFoundException e = assertThrows(FilmNotFoundException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                filmService.getFilmStorage().findFilm(999);
            }
        });
        assertEquals("Фильм с таким 999 не найден.", e.getMessage());
    }

    @Test
    public void addLikeAndDeleteTest() {
        filmService.addLike(film1.getId(),user1.getId());
        assertTrue(film1.getLikes().contains(user1.getId()),"ID  пользователя отсутствует в лайках фильма");

        final UserNotFounfException e = assertThrows(UserNotFounfException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                filmService.addLike(film1.getId(), 555);
            }
        });
        assertEquals("Пользователь с ID = 555 не найден.", e.getMessage());

        final UserNotFounfException ex = assertThrows(UserNotFounfException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                filmService.deleteLike(film1.getId(),555);
            }
        });
        assertEquals("Пользователь с ID = 555 не найден.", ex.getMessage());

        filmService.deleteLike(film1.getId(),user1.getId());
        assertEquals(film1.getLikes().size(),0, "список не пуст");
    }

    @Test
    public void getPopularFilmTest() {
        User user2 = User.builder()
                .name("Marina2")
                .email("marina@mail2.ru")
                .birthday(LocalDate.of(1995, 8, 22))
                .login("Novak2")
                .build();
        filmService.getUserStorage().createUser(user2);

        User user3 = User.builder()
                .name("Marina3")
                .email("marina@mail3.ru")
                .birthday(LocalDate.of(1995, 8, 22))
                .login("Novak3")
                .build();
        filmService.getUserStorage().createUser(user3);

        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());
        filmService.addLike(film1.getId(), user3.getId());

        filmService.addLike(film2.getId(), user1.getId());
        filmService.addLike(film2.getId(), user1.getId());

        List<Film> mostPoularFilm = filmService.getPopularFilm(1);
        assertEquals(film1, mostPoularFilm.get(0));
        assertEquals(mostPoularFilm.size(),1);

        final FilmNotFoundException ex = assertThrows(FilmNotFoundException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                filmService.getPopularFilm(-1);
            }
        });
        assertEquals("Введено отрицательно значение количества фильмов-1", ex.getMessage());

    }
}


