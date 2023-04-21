package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFounfException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Slf4j
@Service
public class UserService {
    @Autowired
    private final UserStorage storage;


    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    public User addFriends(Integer userId1, Integer userId2) {
        User user1;
        User user2;
        try {
            user1 = storage.getUser(userId1);
            user2 = storage.getUser(userId2);
        }  catch (UserNotFounfException e) {
        throw new UserNotFounfException(e.getMessage());
    }

            if (user1.getFriends() == null) {
                user1.setFriends(new HashSet<>());
            }
            user1.getFriends().add(userId2);

            storage.updateUser(user1);
            log.info("Пользователю с id = {} добавлен друг c ID = {}", user1.getId(), user2.getId());
            return storage.getUser(userId1);
    }

    public User deleteFriends(Integer userId1, Integer userId2) {
        User user1 = storage.getUser(userId1);
        User user2 = storage.getUser(userId2);
        if (user1.getFriends() != null) {
            user1.getFriends().remove(userId2);
        }
        storage.updateUser(user1);
        log.info("Из списка друзей пользователя с ID = {} удален друг с ID = {}", user1.getId(), user2.getId());
        return storage.getUser(userId1);

    }

    public List<User> getFriendsByUser(Integer userId) {
        ArrayList<User> friendsByUser = new ArrayList<>();
        User user = storage.getUser(userId);

        user.getFriends().stream().forEach((id) -> {
            if (id > 0) {
                friendsByUser.add(storage.getUser(id));
            }
        });
        log.info("Получен список друзей пользователя с ID = {}", userId);

        return friendsByUser;
    }

    public List<User> getCommonFriend(Integer userId1, Integer userId2) {
        User user1 = storage.getUser(userId1);
        User user2 = storage.getUser(userId2);
        Set<Integer> uniqFrends = new HashSet<>(user1.getFriends());
        uniqFrends.retainAll(user2.getFriends());
        List<User> userFriends = new ArrayList<>();
        for (Integer id : uniqFrends) {
            userFriends.add(storage.getUser(id));
        }
        log.info("У пользователя с ID = {} найдены общие друзья с пользователем с ID = {}", user1.getId(), user2.getId());
        return userFriends;
    }



    public UserStorage getStorage() {
        return storage;
    }

    public User getUser(Integer id) {
        return storage.getUser(id);
    }

    public List<User> getUsers() {
        return storage.getUsers();
    }

    public User createUser(User user) {
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        for (Integer id : user.getFriends()) {
            if (id < 1 || storage.getUser(id) == null) {
                user.getFriends().remove(id);
            } else {
                addFriends(id, user.getId());
            }
        }
        User newUser = storage.createUser(user);
        log.info("Создан новый пользователь его ID = {}", user.getId());
        return newUser;
    }

    public User updateUser(User user) {
        return storage.updateUser(user);
    }


}
