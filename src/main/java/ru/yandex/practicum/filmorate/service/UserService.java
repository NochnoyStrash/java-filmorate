package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFounfException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    @Autowired
    private UserStorage storage;


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
            return storage.getUser(userId1);
    }

    public User deleteFriends(Integer userId1, Integer userId2) {
        User user1 = storage.getUser(userId1);
        User user2 = storage.getUser(userId2);
        if (user1.getFriends() != null) {
            user1.getFriends().remove(userId2);
        }
        storage.updateUser(user1);
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
        return userFriends;
    }



    public UserStorage getStorage() {
        return storage;
    }
}
