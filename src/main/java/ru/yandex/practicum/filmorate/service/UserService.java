package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFounfException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class UserService {
    private UserStorage storage;

    @Autowired
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
            if (user2.getFriends() == null) {
                user2.setFriends(new HashSet<>());
            }
            user2.getFriends().add(userId1);
            return storage.getUser(userId1);
    }

    public User deleteFriends(Integer userId1, Integer userId2) {
        User user1 = storage.getUser(userId1);
        User user2 = storage.getUser(userId2);
        if (user1.getFriends() == null) {
            user1.setFriends(new HashSet<>());
        } else if (user2.getFriends() == null) {
            user2.setFriends(new HashSet<>());
        } else {
            user1.getFriends().remove(userId2);
            user2.getFriends().remove(userId1);
        }
        return storage.getUser(userId1);

    }

    public List<User> getFriendsByUser(Integer userId) {
        ArrayList<User> friendsByUser = new ArrayList<>();
        User user = storage.getUser(userId);

        user.getFriends().stream().forEach((id) -> {
            if (id > 0)
            friendsByUser.add(storage.getUser(id));
        });

        return friendsByUser;
    }

    public List<User> getCommonFriend(Integer userId1, Integer userId2) {
        ArrayList<User> commonFriends = new ArrayList<>();
        User user = storage.getUser(userId2);
        user.getFriends().stream().filter(i -> i > 1)
                .filter(i -> storage.getUser(i).getFriends().contains(userId1))
                .forEach(i -> commonFriends.add(storage.getUser(i)));
        return commonFriends;
    }



    public UserStorage getStorage() {
        return storage;
    }
}
