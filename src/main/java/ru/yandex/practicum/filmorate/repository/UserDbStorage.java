package ru.yandex.practicum.filmorate.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFounfException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static ru.yandex.practicum.filmorate.service.ValidationClass.validateUser;

@Slf4j
@Component
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> getUsers() {
        List<User> allUsers = jdbcTemplate.query("SELECT *  FROM USERS u LEFT JOIN USER_FRENDS uf ON u.ID = uf.USER_ID", getRowMappers());
        Set<User> unikUsers = new HashSet<>(allUsers);
        List<User> users = new ArrayList<>();
        for (User user : unikUsers) {
            users.add(getUser(user.getId()));
        }
        return users;
    }

    public User createUser(User user) {
        if(getUsers().contains(user)) {
            log.info("Пользователь {} уже есть в базе", user.getLogin());
            throw new ValidationException("Пользователь уже есть в базе");
        }
        validateUser(user);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement =connection.prepareStatement("INSERT INTO USERS (email, login, NAME, BIRTHDAY)  VALUES (?, ?, ?, ?)", new String[]{"id"});
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getLogin());
            preparedStatement.setString(3, user.getName());
            preparedStatement.setDate(4,Date.valueOf(user.getBirthday()));
            return preparedStatement;
        }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
        return getUser(user.getId());
    }

    public User getUser(Integer id) {
        User user = jdbcTemplate.queryForObject("SELECT * FROM USERS u LEFT JOIN USER_FRENDS uf ON u.ID = uf.USER_ID WHERE ID = ?"
                , getRowMapper(), id);

        return user;
    }

    public User updateUser(User user) {
        validateUser(user);
        String login = user.getLogin();
        if(!getUsers().contains(user)) {
            log.info("Пользователь {} не найден", login);
            throw new UserNotFounfException("Пользователь " + login + "не найден");
        }

        jdbcTemplate.update("UPDATE USERS SET EMAIL= ?, LOGIN = ?, NAME =?, BIRTHDAY = ? WHERE  id = ?", user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        if (user.getFriends() != null) {
            for (Integer id : user.getFriends()) {
                if (id > 0) {
                    jdbcTemplate.update("INSERT into USER_FRENDS (user_id, USER_FRIENDS_ID) values (?, ?)", user.getId(), id);
                }
            }
        }


        return getUser(user.getId());
    }

    public List<User> deleteUser(Integer id) {
        jdbcTemplate.update("DELETE from users where id =?", id);
        return getUsers();
    }

    private RowMapper<User> getRowMapper() {
        return new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                  User  user = User.builder().email(rs.getString("EMAIL"))
                          .name(rs.getString("NAME"))
                          .birthday(rs.getDate("BIRTHDAY").toLocalDate())
                          .id(rs.getInt("ID"))
                          .login(rs.getString("LOGIN"))
                          .friends(new HashSet<>())
                          .build();
                do {
                        if(rs.getInt("USER_FRIENDS_ID") > 0) {
                            user.getFriends().add(rs.getInt("USER_FRIENDS_ID"));
                        }

                } while (rs.next());
                  return user;
            }
        };
    }

    private RowMapper<User> getRowMappers() {
        return new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                    User user = User.builder().email(rs.getString("EMAIL"))
                            .name(rs.getString("NAME"))
                            .birthday(rs.getDate("BIRTHDAY").toLocalDate())
                            .id(rs.getInt("ID"))
                            .login(rs.getString("LOGIN"))
                            .friends(new HashSet<>())
                            .build();

                    return user;
                }
        };
    }

}
