package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class RatingController {
    private final FilmService filmService;

    @Autowired
    public RatingController(FilmService service) {
        this.filmService = service;
    }

    @GetMapping()
    public List<Rating> getRatings() {
        return filmService.getRatings();
    }

    @GetMapping("/{id}")
    public Rating getRating(@PathVariable Integer id) {
        return filmService.getMpa(id);
    }
}
