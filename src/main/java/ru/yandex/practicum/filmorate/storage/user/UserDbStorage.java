package ru.yandex.practicum.filmorate.storage.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


@Component("userDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAllUsers() {
        String sqlQuery = "select user_id, email, login, name, birthday from users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User addUser(User user) {

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        int userID = simpleJdbcInsert.executeAndReturnKey(user.toMap()).intValue();
        user.setId(userID);

        log.info("Добавлен новый пользователь {}", user);

        return user;
    }

    @Override
    public boolean updateUser(User user) {

        String sqlQuery = "update users set " +
                "email = ?, login = ?, name = ?, birthday = ? " +
                "where user_id = ?";

        return jdbcTemplate.update(sqlQuery
                , user.getEmail()
                , user.getLogin()
                , user.getName()
                , user.getBirthday()
                , user.getId())
                > 0;
    }

    @Override
    public User getUserByID(int userID) {
        User foundUser;
        String sqlQuery = "select user_id, email, login, name, birthday " +
                "from users where user_id = ?";

        try {
            foundUser = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, userID);
        } catch (EmptyResultDataAccessException e) {
            log.info(e.getMessage());
            return null;
        }
        return foundUser;
    }

    @Override
    public List<User> getFriends(int userID) {
        String sqlQuery = "select u.USER_ID, u.EMAIL, u.LOGIN, u.NAME, u.BIRTHDAY " +
                "from USERS u " +
                "where USER_ID in " +
                "      (select f.USER_TO_ID " +
                "       from FRIENDSHIP f " +
                "       where f.USER_FROM_ID = ?)";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userID);
    }

    @Override
    public List<User> getCommonFriends(int userID, int otherUserID) {
        String sqlQuery = "select u.USER_ID, u.EMAIL, u.LOGIN, u.NAME, u.BIRTHDAY " +
                "from USERS u " +
                "where USER_ID in " +
                "(select f1.USER_TO_ID from FRIENDSHIP f1 " +
                "join FRIENDSHIP f2 on f1.USER_TO_ID = f2.USER_TO_ID " +
                "where f1.USER_FROM_ID = ? " +
                "and f2.USER_FROM_ID = ? );";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userID, otherUserID);
    }

    @Override
    public boolean checkIfFriends(int userID, int friendId) {
        String sqlQuery = "select USER_FROM_ID from FRIENDSHIP " +
                "where USER_FROM_ID = ? and USER_TO_ID = ?";
        List<Integer> resultList = jdbcTemplate.queryForList(sqlQuery, Integer.class, userID, friendId);
        return resultList.size() > 0;
    }

    @Override
    public boolean addFriend(int userID, int friendID) {
        String sqlQuery = "insert into FRIENDSHIP (USER_FROM_ID, USER_TO_ID) " +
                "VALUES (?, ?);";

        return jdbcTemplate.update(sqlQuery, userID, friendID) > 0;
    }

    @Override
    public boolean deleteFriend(int userID, int friendID) {
        String sqlQuery = "delete from FRIENDSHIP where USER_FROM_ID = ? and USER_TO_ID = ?";
        return jdbcTemplate.update(sqlQuery, userID, friendID) > 0;
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("user_id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }
}
