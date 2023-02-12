package ru.yandex.practicum.filmorate.storage.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private int userID = 0;
    private final Map<Integer, User> users = new HashMap<>();
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public List<User> findAllUsers() {
        log.info("Всего пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User addUser(@RequestBody User user) {
        int id = ++userID;
        user.setId(id);
        users.put(id, user);
        log.info("Добавлен новый пользователь {}", user);
        return user;
    }

    @Override
    public User updateUser(@RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь с таким ID не найден.");
        }

        users.put(user.getId(), user);
        log.info("Пользователь обновлён {}", user);
        return user;
    }

    @Override
    public User getUserByID(int userID) {
        return users.get(userID);
    }

    @Override
    public List<User> getCommonFriends(int userID, int otherUserID) {

        return users.get(userID).getFriends().stream()
                .filter(users.get(otherUserID).getFriends()::contains)
                .map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getFriends(int userID) {
        return users.get(userID).getFriends().stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

}
