package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    UserController uc = new UserController();

    @Test
    void allFieldsCorrect(){
        User user = new User("m@m.m", "loginUser1");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        user.setName("Ivan Ivanov");

        uc.validateUser(user);

        assertEquals("m@m.m", user.getEmail());
        assertEquals("loginUser1", user.getLogin());
        assertEquals("Ivan Ivanov", user.getName());
        assertEquals(LocalDate.of(1990, 1, 1), user.getBirthday());
    }

    @Test
    void emailEmpty() {
        User user = new User("", "login1");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> uc.validateUser(user));

        assertEquals(ValidationException.class, exception.getClass());
        assertEquals("User Email is not valid: ", exception.getMessage());
    }

    @Test
    void emailIncorrect() {
        User user = new User("incorrect.email", "login1");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> uc.validateUser(user));

        assertEquals(ValidationException.class, exception.getClass());
        assertEquals("User Email is not valid: incorrect.email", exception.getMessage());

    }

    @Test
    void loginEmpty() {
        User user = new User("my@email.m", "");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> uc.validateUser(user));

        assertEquals(ValidationException.class, exception.getClass());
        assertEquals("User Login is not valid: ", exception.getMessage());

    }

    @Test
    void loginWhiteSpace() {
        User user = new User("m@email.m", "login with white spaces");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> uc.validateUser(user));

        assertEquals(ValidationException.class, exception.getClass());
        assertEquals("User Login is not valid: login with white spaces", exception.getMessage());

    }

    @Test
    void loginNull() {
        User user = new User("m@m.m", null);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> uc.validateUser(user));

        assertEquals(ValidationException.class, exception.getClass());
        assertEquals("User Login is not valid: null", exception.getMessage());

    }

    @Test
    void nameEmpty() {
        User user = new User("m@m.m", "loginUser1");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        uc.validateUser(user);

        assertEquals(user.getLogin(), user.getName());
    }

@Test
    void birthdayInFutere(){
    User user = new User("m@m.m", "loginUser1");
    user.setBirthday(LocalDate.of(2990, 1, 1));

    ValidationException exception = assertThrows(
            ValidationException.class,
            () -> uc.validateUser(user));

    assertEquals(ValidationException.class, exception.getClass());
    assertEquals("User Birthdate is not valid: 2990-01-01", exception.getMessage());
}


}
