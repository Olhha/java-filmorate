package ru.yandex.practicum.filmorate.storage.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component("inMemoryUserStorage")
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
    public boolean updateUser(@RequestBody User user) {
        if (users.put(user.getId(), user) == null) {
            return false;
        }
        log.info("Пользователь обновлён {}", user);
        return true;
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

    @Override
    public boolean checkIfFriends(int userID, int friendId) {
        return false;
    }

    @Override
    public boolean addFriend(int userID, int friendID) {
        return false;
    }

    @Override
    public boolean deleteFriend(int userID, int friendID) {
        return true;
    }
}
