package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class UserControllerValidationTest {

    UserController controller;

    @BeforeEach
    public void beforeEach() {
        controller = new UserController();
    }


    @Test
    public void createUser() throws ValidationException {
        User user = User.builder().name("Marina")
                .email("marina@mail.ru")
                .birthday(LocalDate.of(1995,8,22))
                .login("Novak")
                .build();
        controller.createUser(user);
        assertEquals(user, controller.getUsers().get(0));

        User user1 = User.builder()
                .login("")
                .email("strong@mail.ru")
                .name("Madama")
                .birthday(LocalDate.of(1996,7,25))
                .build();
        final ValidationException e = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                controller.createUser(user1);
            }
        });
        assertEquals("Валидация не пройдена", e.getMessage());

        User user2 = User.builder()
                .login("Promo")
                .email("")
                .birthday(LocalDate.of(1994,10,25))
                .build();
        final ValidationException ex = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                controller.createUser(user2);
            }
        });
        assertEquals("Валидация не пройдена", ex.getMessage());

        User user3 = User.builder()
                .login("Trava")
                .email("trava@mail.ru")
                .birthday(LocalDate.of(2222,10,25))
                .build();
        final ValidationException exep = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                controller.createUser(user3);
            }
        });
        assertEquals("Валидация не пройдена", ex.getMessage());
    }

    @Test
    public void updateTest() throws ValidationException {
        User user = User.builder().name("Marina")
                .email("marina@mail.ru")
                .birthday(LocalDate.of(1995,8,22))
                .login("Novak")
                .build();
        controller.createUser(user);
        user.setLogin("");
        final ValidationException e = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                controller.updateUser(user);
            }
        });
        assertEquals("Валидация не пройдена", e.getMessage());

        user.setLogin("Tunder");
        user.setName("");
        controller.updateUser(user);
        assertEquals(user.getName(), user.getLogin(), "имя не совпадает с логином");

        user.setEmail("");
        final ValidationException ex = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                controller.updateUser(user);
            }
        });
        assertEquals("Валидация не пройдена", ex.getMessage());
        user.setEmail("panama@mail.ru");
        user.setBirthday(LocalDate.of(2028,5,16));
        final ValidationException exep = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                controller.updateUser(user);
            }
        });
        assertEquals("Валидация не пройдена", exep.getMessage());

    }
}
