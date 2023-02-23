package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
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
        checkIfUserExists(user.getId());

        validateUser(user);
        userStorage.updateUser(user);

        return user;
    }

    private void validateUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }

    public User getUserByID(int userID) {
        User user = userStorage.getUserByID(userID);
        if (user == null) {
            throw new NotFoundException("User with id = " + userID + " doesn't exist.");
        }
        log.info("User with id = " + userID + " found.");
        return user;
    }

    public void checkIfUserExists(int userID) {
        getUserByID(userID);
    }

    public List<User> getFriends(int userID) {
        checkIfUserExists(userID);
        return userStorage.getFriends(userID);
    }

    public boolean addFriend(int userID, int friendID) {
        checkIfUserExists(userID);
        checkIfUserExists(friendID);

        if (userStorage.checkIfFriends(userID, friendID)) {
            throw new AlreadyExistException(String.format("Пользователь %d уже дружит с %d.",
                    userID, friendID));
        }

        return userStorage.addFriend(userID, friendID);

    }

    public boolean deleteFriend(int userID, int friendID) {
        checkIfUserExists(userID);
        checkIfUserExists(friendID);

        if (!userStorage.checkIfFriends(userID, friendID)) {
            throw new NotFoundException(String.format("У пользователя id = %d нет " +
                    "в друзьях id = %d", userID, friendID));
        }

        return userStorage.deleteFriend(userID, friendID);
    }

    public List<User> getCommonFriends(int userID, int otherUserID) {
        checkIfUserExists(userID);
        checkIfUserExists(otherUserID);

        return userStorage.getCommonFriends(userID, otherUserID);
    }
}
