package ru.yandex.practicum.filmorate.model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
public class Film {
    private int id;
    @EqualsAndHashCode.Exclude
    private String name;
    @EqualsAndHashCode.Exclude
    private String description;

    @EqualsAndHashCode.Exclude
    private int duration;

    @EqualsAndHashCode.Exclude
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate releaseDate;

}