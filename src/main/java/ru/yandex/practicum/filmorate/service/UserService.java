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
    private UserStorage storage;

    @Autowired
    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    public User addFriends(Integer userId1, Integer userId2) {
        try {
            storage.getUser(userId1);
            storage.getUser(userId2);
        }  catch (UserNotFounfException e) {
        throw new UserNotFounfException(e.getMessage());
    }

            if (storage.getUser(userId1).getFriends() == null) {
                storage.getUser(userId1).setFriends(new HashSet<>());
            }
            storage.getUser(userId1).getFriends().add(userId2);
            if (storage.getUser(userId2).getFriends() == null) {
                storage.getUser(userId2).setFriends(new HashSet<>());
            }
            storage.getUser(userId2).getFriends().add(userId1);
            return storage.getUser(userId1);
    }

    public User deleteFriends(Integer userId1, Integer userId2) {
        if (storage.getUser(userId1).getFriends() == null) {
            storage.getUser(userId1).setFriends(new HashSet<>());
        } else if (storage.getUser(userId2).getFriends() == null) {
            storage.getUser(userId2).setFriends(new HashSet<>());
        } else {
            storage.getUser(userId1).getFriends().remove(userId2);
            storage.getUser(userId2).getFriends().remove(userId1);
        }
        return storage.getUser(userId1);

    }

    public List<User> getFriendsByUser(Integer userId) {
        ArrayList<User> friendsByUser = new ArrayList<>();
        User user = storage.getUser(userId);

        for (Integer ids : user.getFriends()) {
            if (ids > 0) {
                friendsByUser.add(storage.getUser(ids));
            }
        }
        return friendsByUser;
    }

    public List<User> getCommonFriend(Integer userId1, Integer userId2) {
        ArrayList<User> commonFriends = new ArrayList<>();
        User user = storage.getUser(userId2);
        for (Integer ids : user.getFriends())
            if(ids > 1) {
                if (storage.getUser(ids).getFriends().contains(userId1)) {
                    commonFriends.add(storage.getUser(ids));
                }
            }
        return commonFriends;
    }



    public UserStorage getStorage() {
        return storage;
    }
}
