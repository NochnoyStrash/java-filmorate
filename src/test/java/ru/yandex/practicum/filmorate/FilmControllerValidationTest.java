package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmStorage;
import ru.yandex.practicum.filmorate.repository.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.repository.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.repository.UserStorage;
import ru.yandex.practicum.filmorate.service.FilmService;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

public class FilmControllerValidationTest {
    static String MAX_WORDS = "fajlsjdflajjlfa;lnl;lfjlaf;laf;jaflalfaljlfjlajlfjljfj;lfl;a" +
            "klfkladfadf;ljdfalkn;lkn;akvnkdvka;jndlanfdkljalkfjlkjfkajfjsdfljl;" +
            "fja;ldf;alknfd;laknf;laf;lakf;lkafkllfl;alakdf;alfl;fl;fla;ldfja;lkj" +
            "dffadfadfafaffajkvhjkjkhkjlkgkhvjbknlml;l;;lgvkhjbjnkml''lk;kjlkhgjjbn" +
            "jhjvjbknlm;,'mnbvcvvbjnklk;l;kl;jkhgfcgvhbjnk;ml;',;mnlbkvjhcgxhcjvhbkj";
    FilmController controller;
    @BeforeEach
    public void beforeEach() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        UserStorage userStorage = new InMemoryUserStorage();
        controller = new FilmController( new FilmService(filmStorage,userStorage));
    }

    @Test
    public void testCreateFilm()  {
        Film film = Film.builder()
                .name("Моя прекрасная няня")
                .description("фильм про молодую няню")
                .releaseDate(LocalDate.of(2004,5,15))
                .duration(45)
                .build();
        controller.addFilm(film);
        assertEquals(controller.getFilms().get(0).getName(), film.getName());

        Film film1 = Film.builder()
                .name("")
                .description("фильм о бесконечной любви котов к флаконам на полке")
                .releaseDate(LocalDate.of(1906, 12,5))
                .duration(25)
                .build();
        final ValidationException e = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                controller.addFilm(film1);
            }
        });
        assertEquals("Фильм не прошел валидацию. Название не может быть пустым", e.getMessage());

        Film film2 = Film.builder()
                .name("Сверхъествественное")
                .description(MAX_WORDS)
                .duration(158)
                .releaseDate(LocalDate.of(1958,6,8))
                .build();
        final ValidationException ex = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                controller.addFilm(film2);
            }
        });
        assertEquals("Фильм " + film2.getName() + " не прошел валидацию. Более 200 символов", ex.getMessage());

        Film film3 = Film.builder()
                .name("Дача")
                .description("как ухаживать за дачей")
                .releaseDate(LocalDate.of(1875, 10,5))
                .duration(15)
                .build();
        final ValidationException exep = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                controller.addFilm(film3);
            }
        });
        assertEquals("Фильм " + film3.getName() + " не прошел валидацию.Дата создания должна быть позже 1895.12.28", exep.getMessage());

        Film film4 = Film.builder()
                .name("Ночь")
                .description("Они появляются во тьме...")
                .releaseDate(LocalDate.of(2005,11,11))
                .duration(-106)
                .build();
        final ValidationException exeption = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                controller.addFilm(film4);
            }
        });
        assertEquals("Фильм " + film4.getName() + " не прошел валидацию. Продожительность не может быть отрицательной", exeption.getMessage());
    }

    @Test
    public void testUpdateFilm() {
        Film film = Film.builder()
                .name("Моя прекрасная няня")
                .description("фильм про молодую няню")
                .releaseDate(LocalDate.of(2004,5,15))
                .duration(45)
                .build();
        controller.addFilm(film);

        film.setName("");
        final ValidationException e = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                controller.updateFilm(film);
            }
        });
        assertEquals("Фильм не прошел валидацию. Название не может быть пустым", e.getMessage());

        film.setName("Варяг");
        film.setDescription(MAX_WORDS);
        final ValidationException ex = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                controller.updateFilm(film);
            }
        });
        assertEquals("Фильм " + film.getName() + " не прошел валидацию. Более 200 символов", ex.getMessage());

        film.setDescription("там дигидам дам дам");
        film.setReleaseDate(LocalDate.of(1856,10,16));
        final ValidationException exep = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                controller.updateFilm(film);
            }
        });
        assertEquals("Фильм " + film.getName() + " не прошел валидацию.Дата создания должна быть позже 1895.12.28", exep.getMessage());

        film.setReleaseDate(LocalDate.of(2009,12,31));
        film.setDuration(-90);
        final ValidationException exeption = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                controller.updateFilm(film);
            }
        });
        assertEquals("Фильм " + film.getName() + " не прошел валидацию. Продожительность не может быть отрицательной", exeption.getMessage());



    }
}
