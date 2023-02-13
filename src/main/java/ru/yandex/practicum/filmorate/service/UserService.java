package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final Logger log = LoggerFactory.getLogger(getClass());


    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    public User addUser(User user) {
        validateUser(user);
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        validateUser(user);

        if (!userStorage.updateUser(user)) {
            throw new NotFoundException("Пользователь с таким ID не найден.");
        }

        return user;
    }

    private void validateUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }

    public User getUserByID(int userID) {
        return checkIfUserExists(userID);
    }

    public User checkIfUserExists(int userID) {
        User user = userStorage.getUserByID(userID);
        if (user == null) {
            throw new NotFoundException("User with id = " + userID + " doesn't exist.");
        }
        log.info("User with id = " + userID + " found.");
        return user;
    }

    public List<User> getFriends(int userID) {
        checkIfUserExists(userID);
        return userStorage.getFriends(userID);
    }

    public User addFriend(int userID, int friendId) {
        User user = getUserByID(userID);
        User friend = getUserByID(friendId);

        if (!user.addFriend(friendId) || !friend.addFriend(userID)) {
            throw new AlreadyExistException(String.format("Пользователь %d уже дружит с %d.",
                    userID, friendId));
        }

        return user;
    }

    public User deleteFriend(int userID, int friendId) {

        User user = getUserByID(userID);
        User friend = getUserByID(friendId);

        if (!user.deleteFriend(friendId) || !friend.deleteFriend(userID)) {
            throw new NotFoundException(String.format("У пользователя id = %d нет в друзьях id = %d",
                    userID, friendId));
        }

        return user;
    }

    public List<User> getCommonFriends(int userID, int otherUserID) {
        checkIfUserExists(userID);
        checkIfUserExists(otherUserID);

        return userStorage.getCommonFriends(userID, otherUserID);
    }
}
