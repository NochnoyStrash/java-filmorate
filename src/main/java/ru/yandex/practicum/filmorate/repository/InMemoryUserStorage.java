package ru.yandex.practicum.filmorate.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFounfException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static ru.yandex.practicum.filmorate.service.ValidationClass.validateUser;


@Slf4j

public class InMemoryUserStorage implements  UserStorage {
    private int id = 1;
    private List<User> users = new ArrayList<>();

    public List<User> getUsers() {
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

    public User updateUser(User user) {
        validateUser(user);
        String login = user.getLogin();
        if (!users.contains(user)) {
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

    public List<User> deleteUser(Integer id) {
        return null;
    }


}
