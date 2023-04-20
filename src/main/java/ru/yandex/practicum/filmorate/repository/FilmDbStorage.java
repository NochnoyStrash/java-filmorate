package ru.yandex.practicum.filmorate.repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.service.ValidationClass.validateFilms;

@Slf4j
@Component
@Primary
public class FilmDbStorage implements  FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public List<Film> getFilms() {
        List<Integer> numbers = getNumbers();
        List<Film> films = new ArrayList<>();
        for (Integer id : numbers) {
            films.add(findFilm(id));
        }
        return films.stream().sorted((o, o2) -> o.getId() - o2.getId()).collect(Collectors.toList());
    }


    public Film addFilm(Film film) {
        validateFilms(film);
        if (getFilms().contains(film)) {
            log.info("Фильм  уже есть в списке");
            throw new ValidationException("Фильм  уже есть в списке");
        }
        int idRat = film.getMpa().getId();
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO FILMS (NAME, DESCRIPTION, DURATION, RELEASEDATE, RATING) " +
                    "VALUES (?, ?, ?, ?, ?)", new String[]{"id"});
            preparedStatement.setString(1, film.getName());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setInt(3, film.getDuration());
            preparedStatement.setDate(4, Date.valueOf(film.getReleaseDate()));
            preparedStatement.setInt(5, idRat);
            return preparedStatement;
    }, keyHolder);
        int id = keyHolder.getKey().intValue();
        film.setId(id);
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("insert into films_genre (id_films, id_genre) values (?, ?)",
                        film.getId(), genre.getId());
            }
        }
        return findFilm(id);
    }

    public Film updateFilm(Film film) {
        validateFilms(film);
        if (!getNumbers().contains(film.getId())) {
            log.info("Фильм с id {} не найден", film.getId());
            throw new FilmNotFoundException("Фильм не найден");
        }
        jdbcTemplate.update("UPDATE films SET name = ?, DESCRIPTION = ?, DURATION = ?, RELEASEDATE = ?, RATING = ? where id = ?",
                film.getName(), film.getDescription(), film.getDuration(), film.getReleaseDate().toString(), film.getMpa().getId(), film.getId());
        if (film.getLikes() == null || film.getLikes().isEmpty()) {
            jdbcTemplate.update("DELETE FROM FILMS_LIKES WHERE FILM_ID = ?", film.getId());
        } else {
            jdbcTemplate.update("DELETE FROM FILMS_LIKES WHERE FILM_ID = ?", film.getId());
            for (Integer id : film.getLikes()) {
                jdbcTemplate.update("MERGE INTO FILMS_LIKES KEY (FILM_ID, USER_ID) VALUES (?,?)", film.getId(), id);
            }
        }

        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            jdbcTemplate.update("DELETE FROM FILMS_GENRE WHERE id_films = ?", film.getId());
        } else {
            jdbcTemplate.update("DELETE FROM FILMS_GENRE WHERE id_films = ?", film.getId());
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("MERGE INTO FILMS_GENRE KEY (ID_FILMS, ID_GENRE) values (?, ?)",
                        film.getId(), genre.getId());
            }
        }
        return findFilm(film.getId());
    }

    public Film findFilm(Integer id) throws FilmNotFoundException {
        if (!getNumbers().contains(id)) {
            throw new FilmNotFoundException("Фильм с ID = " + id + " не найден.");
        }
        Film film  = jdbcTemplate.queryForObject("SELECT *, r.NAME AS ratingName FROM FILMS f LEFT " +
                "JOIN FILMS_LIKES fl ON f.ID =fl.FILM_ID LEFT JOIN FILMS_GENRE fg ON fg.id_films =f.id " +
                "JOIN RATING r ON r.ID_RATING =f.rating where id = ?", getRM(), id);

        return  film;
    }

    public List<Genre> getGenres() {
        List<Genre> genres = jdbcTemplate.query("Select * from genre", getRowGenre());
        return genres;
    }

    public Genre getGenre(Integer id) {
        if (!getGenres().stream().anyMatch(genre -> genre.getId() == id)) {
            throw new  FilmNotFoundException("Жанра с таким ID = " + id + " не найден.");
        }
         Genre genre = jdbcTemplate.queryForObject("Select * from genre where id_genre = ?", getRowGenre(), id);
        return genre;
    }

    public List<Rating> getRatings() {
        List<Rating> ratings = jdbcTemplate.query("Select * from rating", getRowRating());
        return ratings;
    }

    public Rating getMPA(Integer id) {
        if (!getRatings().stream().anyMatch(rating -> rating.getId() == id)) {
            throw  new FilmNotFoundException("Рейтинга с таким ID = " + id + " не найдено.");
        }
        Rating rating = jdbcTemplate.queryForObject("Select * from rating where id_rating = ?", getRowRating(), id);
        return rating;
    }

    private List<Integer> getNumbers() {
        return  jdbcTemplate.query("SELECT id FROM films", new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt("id");
            }
        });
    }

    private RowMapper<Rating> getRowRating() {
        return new RowMapper<Rating>() {
            @Override
            public Rating mapRow(ResultSet rs, int rowNum) throws SQLException {
                Rating mpa = Rating.builder().build();
                mpa.setId(rs.getInt("id_rating"));
                mpa.setName(rs.getString("name"));
                return mpa;
            }
        };
    }

    private RowMapper<Genre> getRowGenre() {
        return new RowMapper<Genre>() {
            @Override
            public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
                Genre genre = new Genre();
                genre.setId(rs.getInt("id_genre"));
                genre.setName(rs.getString("name"));
                return genre;
            }
        };
    }



    private RowMapper<Film> getRM() {
        return new RowMapper<Film>() {
            @Override
            public Film mapRow(ResultSet rs, int rowNum) throws SQLException {

                Rating mpa = getMPA(rs.getInt("RATING"));
                Film film = Film.builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .description(rs.getString("DESCRIPTION"))
                        .duration(rs.getInt("DURATION"))
                        .genres(new HashSet<>())
                        .likes(new HashSet<>())
                        .releaseDate(rs.getDate("RELEASEDATE").toLocalDate())
                        .mpa(mpa)
                        .build();
                do {
                    int idGenre = rs.getInt("id_genre");
                    if (idGenre > 0) {
                      Genre genre = getGenre(idGenre);
                        film.getGenres().add(genre);
                    }

                    if (rs.getInt("USER_ID") > 0) {
                        film.getLikes().add(rs.getInt("USER_ID"));
                    }
                } while (rs.next());

                return film;
            }
        };
    }

}
