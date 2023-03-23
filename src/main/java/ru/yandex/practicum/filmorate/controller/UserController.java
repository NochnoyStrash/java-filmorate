package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@RestController

@Slf4j
public class UserController {
    private int id = 1;
    private List<User> users = new ArrayList<>();

    @GetMapping("/users")
    public List<User> getUsers(){
        log.debug("Пользователей в списке: {}", users.size());
        return users;
    }

    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        validateUser(user);
        if (users.contains(user)) {
            log.info("Пользователь {} уже есть в базе", user.getLogin());
            throw new ValidationException("Пользователь уже есть в базе");
        }
        log.info("Пользователь {} успешно сохранен", user.getLogin());
        user.setId(id);
        users.add(user);
        id++;
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User user) {
        validateUser(user);
        if(!users.contains(user)) {
            log.info("Пользователь {} не найден", user.getLogin());
            throw new ValidationException("Пользователь " + user.getLogin() + "не найден");
        }
        users.remove(user);
        users.add(user);
        log.info("Пользователь {} успешно обновлен", user.getLogin());
        return user;
    }

    private void validateUser(User user) {
        if(user.getLogin() == null || (user.getLogin().isBlank()) || user.getLogin().contains(" ") ) {
            log.info("Валидация не пройдена. Login не может быть пустым или содержать пробелы");
            throw new ValidationException("Валидация не пройдена. Login не может быть пустым или содержать пробелы");
        }
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.info("Валидация {} не пройдена. Введите корректный email", user.getEmail());
            throw new ValidationException("Валидация не пройдена. Введите корректный email");
        }


        if (user.getBirthday() != null) {
            if (user.getBirthday().isAfter(LocalDate.now())) {
                log.info("Валидация {} не пройдена. День рождения не может быть позже текущего времени", user.getBirthday());
                throw new ValidationException("Валидация не пройдена. День рождения не может быть позже текущего времени");
            }
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
