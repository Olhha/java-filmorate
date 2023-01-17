package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int userID = 0;
    private final static Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public List<User> findAllUsers() {
        log.info("Всего пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        validateUser(user);
        int id = ++userID;
        user.setId(id);
        users.put(id, user);
        log.info("Добавлен новый пользователь {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        validateUser(user);

        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Пользователь с таким ID не найден.");
        }

        users.put(user.getId(), user);
        log.info("Пользователь обновлён {}", user);
        return user;
    }


    private void validateUser(User user) {
        if (user == null) {
            throw new ValidationException("User can't be empty");
        }

        if (!user.getEmail().contains("@")) {
            throw new ValidationException("User Email is not valid: " + user.getEmail());
        }

        if (user.getLogin().isBlank()) {
            throw new ValidationException("User Login is not valid: " + user.getLogin());
        }

        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("User Birthdate is not valid: " + user.getBirthday());
        }

    }
}
