package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.HashSet;
import java.util.List;


@RestController

@Slf4j
public class UserController {
    private UserService service;

    @Autowired
    public UserController(UserService  service) {
        this.service = service;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return service.getStorage().getUsers();
    }

    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        User newUser = service.getStorage().createUser(user);
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        for (Integer id : user.getFriends()) {
            if (id < 1 || service.getStorage().getUser(id) == null) {
                user.getFriends().remove(id);
            } else {
                service.addFriends(id, user.getId());
            }
        }
        return newUser;
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User user) {
        service.getStorage().updateUser(user);

        return user;
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Integer id) {
        return service.getStorage().getUser(id);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public User addFriend(@PathVariable  Integer id, @PathVariable Integer friendId) {
       return service.addFriends(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable  Integer id, @PathVariable Integer friendId) {
        return service.deleteFriends(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriendsByUser(@PathVariable Integer id) {
        return service.getFriendsByUser(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable  Integer id, @PathVariable Integer otherId) {
        return  service.getCommonFriend(id, otherId);
    }
}
