package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.exception.UserNotFounfException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.service.UserService;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserServiceTest {
    UserService userService;
    User user;
    User user1;

    @BeforeEach
    public void beforeEach() {
        userService = new UserService(new InMemoryUserStorage());
        user = User.builder().name("Marina")
                .email("marina@mail.ru")
                .birthday(LocalDate.of(1995,8,22))
                .login("Novak")
                .build();
        user1 = User.builder()
                .login("Purum")
                .email("strong@mail.ru")
                .name("Madama")
                .birthday(LocalDate.of(1996,7,25))
                .build();
        userService.getStorage().createUser(user);
        userService.getStorage().createUser(user1);
    }

    @Test
    public void shouldAddAndDeleteFriendsTest() {
        userService.addFriends(user.getId(),user1.getId());
        assertTrue(user.getFriendsConfirm().contains(user1.getId()));

        final UserNotFounfException e = assertThrows(UserNotFounfException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                userService.addFriends(user.getId(),555);
            }
        });

        assertEquals("Пользователь с ID = 555 не найден.",e.getMessage());

        final UserNotFounfException ex = assertThrows(UserNotFounfException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                userService.deleteFriends(user.getId(), -1);
            }
        });
        assertEquals("Пользователь с ID = -1 не найден.",ex.getMessage());
        userService.deleteFriends(user.getId(), user1.getId());
        System.out.println(user.getFriendsConfirm());
        assertTrue(user.getFriendsConfirm().isEmpty());


    }

    @Test
    public void shouldGetCommonFriends() {
        User user2 = User.builder()
                .login("Promo")
                .email("promo@fop")
                .name("Pestro")
                .birthday(LocalDate.of(1994,10,25))
                .build();

        User user3 = User.builder()
                .login("Taatra")
                .email("grom@fop")
                .name("Vudu")
                .birthday(LocalDate.of(1994,10,25))
                .build();
        userService.getStorage().createUser(user2);
        userService.getStorage().createUser(user3);
        userService.addFriends(1,2);
        userService.addFriends(1,4);
        userService.addFriends(3,2);
        userService.addFriends(3,4);
        ArrayList<User> commonFrends = new ArrayList<>(List.of(user1,user3));

        assertArrayEquals(commonFrends.toArray(), userService.getCommonFriend(1,3).toArray(), " списки не равны");

    }

    @Test
    public void getFriendsByUserTest() {
        User user2 = User.builder()
                .login("Promo")
                .email("promo@fop")
                .name("Pestro")
                .birthday(LocalDate.of(1994,10,25))
                .build();

        User user3 = User.builder()
                .login("Taatra")
                .email("grom@fop")
                .name("Vudu")
                .birthday(LocalDate.of(1994,10,25))
                .build();
        userService.getStorage().createUser(user2);
        userService.getStorage().createUser(user3);
        userService.addFriends(user.getId(),user1.getId());
        userService.addFriends(user.getId(),user2.getId());
        userService.addFriends(user.getId(),user3.getId());
        ArrayList<User> userFriends = new ArrayList<>(List.of(user1, user2, user3));
        assertArrayEquals(userFriends.toArray(), userService.getFriendsByUser(user.getId()).toArray(), "списки не равны");

        final UserNotFounfException ex = assertThrows(UserNotFounfException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                userService.getFriendsByUser(777);
            }
        });
        assertEquals("Пользователь с ID = 777 не найден.", ex.getMessage());
    }



}
