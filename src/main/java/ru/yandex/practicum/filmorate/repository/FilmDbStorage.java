package ru.yandex.practicum.filmorate.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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

import javax.persistence.criteria.CriteriaBuilder;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static ru.yandex.practicum.filmorate.service.ValidationClass.validateFilms;

@Slf4j
@Component
@Qualifier("filmDbStorage")
public class FilmDbStorage implements  FilmStorage{
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Film> getFilms() {
        List<Film> allFilms = jdbcTemplate.query("SELECT *, r.NAME AS ratingName FROM FILMS f LEFT JOIN FILMS_LIKES fl ON f.ID =fl.FILM_ID LEFT JOIN FILMS_GENRE fg ON fg.id_films =f.id JOIN RATING r ON r.ID_RATING =f.rating", getRMs());
        Set<Film> unikFilms = new HashSet<>(allFilms);
        List<Film> films = new ArrayList<>();
        for (Film film : unikFilms) {
            films.add(findFilm(film.getId()));
        }
        return films;
    }


    public Film addFilm(Film film) {
        validateFilms(film);


        if(getFilms().contains(film)) {
            log.info("Фильм  уже есть в списке");
            throw new ValidationException("Фильм  уже есть в списке");
        }
        int idRat = film.getMpa().getId();
        System.out.println(idRat+ "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO FILMS (NAME, DESCRIPTION, DURATION, RELEASEDATE, RATING) VALUES (?, ?, ?, ?, ?)", new String[]{"id"});
            preparedStatement.setString(1, film.getName());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setInt(3, film.getDuration());
            preparedStatement.setDate(4, Date.valueOf(film.getReleaseDate()));
            preparedStatement.setInt(5, idRat );
            return preparedStatement;
    }, keyHolder);
        int id = keyHolder.getKey().intValue();
        film.setId(id);
        if (film.getGenres() != null ) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("insert into films_genre (id_films, id_genre) values (?, ?)",film.getId(),genre.getId());
            }
        }
        return findFilm(id);
    }

    public Film updateFilm(Film film) {
        validateFilms(film);
        if(!getFilms().contains(film)) {
            log.info("Фильм с id {} не найден", film.getId());
            throw new FilmNotFoundException("Фильм не найден");
        }

        jdbcTemplate.update("UPDATE films SET name = ?, DESCRIPTION = ?, DURATION = ?, RELEASEDATE = ?, RATING = ?", film.getName(), film.getDescription(), film.getDuration(), film.getReleaseDate(), film.getMpa().getId());
        if (film.getLikes() != null) {
            for (Integer id : film.getLikes()) {
                jdbcTemplate.update("insert into films_likes (film_id, user_id) values (?, ?)", film.getId(), id);
            }
        }

        if (film.getGenres() != null ) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("insert into films_genre (id_films, id_genre) values (?, ?)",film.getId(), genre.getId());
            }
        }
        return findFilm(film.getId());
    }

    public Film findFilm(Integer id) {
        Film film  = jdbcTemplate.queryForObject("SELECT *, r.NAME AS ratingName FROM FILMS f LEFT JOIN FILMS_LIKES fl ON f.ID =fl.FILM_ID LEFT JOIN FILMS_GENRE fg ON fg.id_films =f.id JOIN RATING r ON r.ID_RATING =f.rating where id = ?", getRM(), id);

        return  film;
    }

    public List<Genre> getGenres()  {
        List<Genre> genres = jdbcTemplate.query("Select * from genre",getRowGenre());
        return genres;
    }

    public Genre getGenre (Integer id) {
        Genre genre = jdbcTemplate.queryForObject("Select * from genre where id_genre = ?", getRowGenre(),id);
        return genre;
    }

    public List<Rating> getRatings() {
        List<Rating> ratings = jdbcTemplate.query("Select * from rating", getRowRating());
        return ratings;
    }

    public Rating getMPA(Integer id) {
        Rating rating = jdbcTemplate.queryForObject("Select * from rating where id_rating = ?", getRowRating(), id);
        return rating;
    }

    private RowMapper<Rating> getRowRating() {
        return new RowMapper<Rating>() {
            @Override
            public Rating mapRow(ResultSet rs, int rowNum) throws SQLException {
                Rating mpa = new Rating();
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
                Rating mpa = new Rating();
                mpa.setId(rs.getInt("RATING"));
                mpa.setName(rs.getString("ratingName"));


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
                    Genre genre = new Genre();
                    if (rs.getInt("id_genre") >0) {
                        genre.setId(rs.getInt("id_genre"));
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


    private RowMapper<Film> getRMs() {
        return new RowMapper<Film>() {
            @Override
            public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
                Rating mpa = new Rating();
                mpa.setId(rs.getInt("RATING"));
                mpa.setName(rs.getString("ratingName"));
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

                return film;
            }
        };
    }
}
