package ru.yandex.practicum.filmorate.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFounfException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class InMemoryUserStorage implements  UserStorage{
    private int id = 1;
    private List<User> users = new ArrayList<>();

    public List<User> getUsers(){
        log.debug("Пользователей в списке: {}", users.size());
        return users;
    }

    public User createUser(User user) {
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

    public User updateUser( User user) {
        validateUser(user);
        String login = user.getLogin();
        if(!users.contains(user)) {
            log.info("Пользователь {} не найден", login);
            throw new UserNotFounfException("Пользователь " + login + "не найден");
        }
        users.remove(user);
        users.add(user);
        log.info("Пользователь {} успешно обновлен", user.getLogin());
        return user;
    }
    public User getUser(Integer id) {
        return users.stream()
                .filter(u -> u.getId() == id)
                .findFirst().orElseThrow(() -> new UserNotFounfException("Пользователь с ID = " + id + " не найден."));
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
