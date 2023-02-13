package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> findAllUsers();

    User addUser(User user);

    boolean updateUser(User user);

    User getUserByID(int userID);

    List<User> getFriends(int userID);

    List<User> getCommonFriends(int userID, int otherUserID);
}
