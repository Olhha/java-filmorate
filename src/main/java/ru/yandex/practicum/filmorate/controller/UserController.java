package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> findAllUsers() {
        return userService.findAllUsers();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @GetMapping("/{userID}")
    public User getUserById(@PathVariable int userID) {
        return userService.getUserByID(userID);
    }

    @GetMapping("/{userID}/friends")
    public List<User> getFriends(@PathVariable int userID) {
        return userService.getFriends(userID);
    }

    @GetMapping("/{userID}/friends/common/{otherUserID}")
    public List<User> getCommonFriends(@PathVariable int userID, @PathVariable int otherUserID){
        return userService.getCommonFriends(userID, otherUserID);
    }

    @PutMapping("/{userID}/friends/{friendID}")
    public User addFriend(@PathVariable int userID, @PathVariable int friendID) {
        return userService.addFriend(userID, friendID);
    }

    @DeleteMapping("/{userID}/friends/{friendID}")
    public User deleteFriend(@PathVariable int userID, @PathVariable int friendID) {
        return userService.deleteFriend(userID, friendID);
    }


}
