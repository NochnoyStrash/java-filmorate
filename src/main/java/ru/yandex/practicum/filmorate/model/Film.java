package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class Film {
    private int id;
    @EqualsAndHashCode.Exclude
    private Set<Integer> likes;
    @EqualsAndHashCode.Include
    private String name;
    @EqualsAndHashCode.Exclude
    private String description;

    @EqualsAndHashCode.Exclude
    private int duration;

    @EqualsAndHashCode.Exclude
    private Set<Genre> genres;

    @EqualsAndHashCode.Exclude
    private Rating mpa;

    @EqualsAndHashCode.Include
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

}
