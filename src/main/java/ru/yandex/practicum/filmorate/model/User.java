package ru.yandex.practicum.filmorate.model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Value;


import java.time.LocalDate;

@Data
@Builder
public class User {
    private int id;
    @EqualsAndHashCode.Exclude
    private String email;
    @EqualsAndHashCode.Exclude
    private String login;
    @EqualsAndHashCode.Exclude
    private String name;
    @EqualsAndHashCode.Exclude
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
}
