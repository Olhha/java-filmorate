package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DAOtests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final JdbcTemplate jdbcTemplate;


    private void createTestData() {

        jdbcTemplate.update("insert into users (email, login, name, birthday) " +
                "values ('testEmail1@m.m' , 'testUserLogin1', 'testName1', '2000-02-02')");

        jdbcTemplate.update("insert into users (email, login, name, birthday) " +
                "values ('testEmail2@m.m' , 'testUserLogin2', 'testName2', '2000-02-02')");

        jdbcTemplate.update("insert into users (email, login, name, birthday) " +
                "values ('testEmail3@m.m' , 'testUserLogin3', 'testName3', '2000-02-02')");

        jdbcTemplate.update("insert into friendship (user_from_id, user_to_id) " +
                "values (1 , 2) , (1, 3), (2, 3)");

        jdbcTemplate.update("insert into films (name, description, release_date, duration, mpa_id) " +
                "values ('testFilm1', 'test film description', '2000-02-02','100','1')");

        jdbcTemplate.update("insert into films (name, description, release_date, duration, mpa_id) " +
                "values ('testFilm2', 'test film description2', '2000-02-02','100','2')");

        jdbcTemplate.update("insert into films (name, description, release_date, duration, mpa_id) " +
                "values ('testFilm3', 'test film description3', '2000-02-02','100','3')");

        jdbcTemplate.update("insert into film_genre (genre_id, film_id) " +
                "values (1,1), (2,1), (3,2) ");
        jdbcTemplate.update("insert into film_likes (film_id, user_id) " +
                "values (1,1), (2,1), (2,2) ");
    }

    @BeforeEach
    public void init() {
        jdbcTemplate.update("DELETE FROM FRIENDSHIP ");
        jdbcTemplate.update("DELETE FROM FILM_LIKES ");
        jdbcTemplate.update("DELETE FROM FILM_GENRE ");

        jdbcTemplate.update("DELETE FROM USERS ");
        jdbcTemplate.update("ALTER TABLE USERS ALTER COLUMN USER_ID RESTART WITH 1");
        jdbcTemplate.update("DELETE FROM FILMS ");
        jdbcTemplate.update("ALTER TABLE FILMS ALTER COLUMN FILM_ID RESTART WITH 1");

        createTestData();
    }

    @Test
    public void testFindUserById() {

        User user = userStorage.getUserByID(1);

        assertThat(user)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("email", "testEmail1@m.m")
                .hasFieldOrPropertyWithValue("login", "testUserLogin1")
                .hasFieldOrPropertyWithValue("name", "testName1")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2000, 2, 2))
        ;
    }

    @Test
    public void testfindAllUsers() {
        List<User> users = userStorage.findAllUsers();

        assertThat(users)
                .hasSize(3);

        assertThat(users.get(0))
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("email", "testEmail1@m.m")
                .hasFieldOrPropertyWithValue("login", "testUserLogin1")
                .hasFieldOrPropertyWithValue("name", "testName1")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2000, 2, 2))
        ;

        assertThat(users.get(1))
                .hasFieldOrPropertyWithValue("id", 2)
                .hasFieldOrPropertyWithValue("email", "testEmail2@m.m")
                .hasFieldOrPropertyWithValue("login", "testUserLogin2")
                .hasFieldOrPropertyWithValue("name", "testName2")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2000, 2, 2))
        ;

        assertThat(users.get(2))
                .hasFieldOrPropertyWithValue("id", 3)
                .hasFieldOrPropertyWithValue("email", "testEmail3@m.m")
                .hasFieldOrPropertyWithValue("login", "testUserLogin3")
                .hasFieldOrPropertyWithValue("name", "testName3")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2000, 2, 2))
        ;

    }

    @Test
    public void testAddUser() {
        User user = User.builder()
                .email("email2@m.m")
                .login("UserLogin2")
                .name("Name2")
                .birthday(LocalDate.of(2005, 2, 2))
                .build();

        User addedUser = userStorage.addUser(user);

        assertThat(addedUser).isEqualTo(user);
    }

    @Test
    public void testUpdateUser() {
        User userForUpdate = User.builder()
                .id(1)
                .email("NewEmail@m.m")
                .login("NewUserLogin")
                .name("NewName")
                .birthday(LocalDate.of(2007, 2, 2))
                .build();

        userStorage.updateUser(userForUpdate);
        User user = userStorage.getUserByID(1);

        assertThat(user).isEqualTo(userForUpdate);
    }

    @Test
    public void testGetFriends() {
        List<User> friends = userStorage.getFriends(1);

        assertThat(friends)
                .hasSize(2);

        assertThat(friends.get(0))
                .hasFieldOrPropertyWithValue("id", 2)
                .hasFieldOrPropertyWithValue("email", "testEmail2@m.m")
                .hasFieldOrPropertyWithValue("login", "testUserLogin2")
                .hasFieldOrPropertyWithValue("name", "testName2")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2000, 2, 2))
        ;

        assertThat(friends.get(1))
                .hasFieldOrPropertyWithValue("id", 3)
                .hasFieldOrPropertyWithValue("email", "testEmail3@m.m")
                .hasFieldOrPropertyWithValue("login", "testUserLogin3")
                .hasFieldOrPropertyWithValue("name", "testName3")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2000, 2, 2))
        ;
    }

    @Test
    public void testGetCommonFriends() {
        List<User> commonFriends = userStorage.getCommonFriends(1, 2);

        assertThat(commonFriends)
                .hasSize(1);

        assertThat(commonFriends.get(0))
                .hasFieldOrPropertyWithValue("id", 3)
                .hasFieldOrPropertyWithValue("email", "testEmail3@m.m")
                .hasFieldOrPropertyWithValue("login", "testUserLogin3")
                .hasFieldOrPropertyWithValue("name", "testName3")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2000, 2, 2))
        ;
    }

    @Test
    public void testCheckIfFriends() {
        boolean friendsOrNot12 = userStorage.checkIfFriends(1, 2);
        boolean friendsOrNot21 = userStorage.checkIfFriends(2, 1);

        assertThat(friendsOrNot12)
                .isTrue();

        assertThat(friendsOrNot21)
                .isFalse();
    }

    @Test
    public void testAddFriend() {
        userStorage.addFriend(2, 1);

        boolean friendsOrNot = userStorage.checkIfFriends(2, 1);

        assertThat(friendsOrNot)
                .isTrue();
    }

    @Test
    public void testDeleteFriend() {
        userStorage.addFriend(2, 1);
        boolean friendsOrNot = userStorage.checkIfFriends(2, 1);

        assertThat(friendsOrNot)
                .isTrue();

        userStorage.deleteFriend(2, 1);

        friendsOrNot = userStorage.checkIfFriends(2, 1);

        assertThat(friendsOrNot)
                .isFalse();
    }

    @Test
    public void testAddFilm() {
        Film film = Film.builder()
                .name("NewFilm")
                .description("new interesting film")
                .mpa(new Mpa(1, "G"))
                .duration(100)
                .releaseDate(LocalDate.of(2002, 2, 2))
                .build();

        Film addedFilm = filmStorage.addFilm(film);

        assertThat(addedFilm).isEqualTo(film);

    }

    @Test
    public void testUpdateFilm() {
        Film filmUpdated = Film.builder()
                .id(1)
                .name("NewFilm")
                .description("new interesting film")
                .mpa(new Mpa(1, "G"))
                .duration(100)
                .releaseDate(LocalDate.of(2002, 2, 2))
                .build();

        filmStorage.updateFilm(filmUpdated);

        assertThat(filmStorage.getFilmByID(1))
                .isEqualTo(filmUpdated);
    }

    @Test
    public void testGetAllFilm() {
        List<Film> films = filmStorage.getAllFilms();

        assertThat(films)
                .hasSize(3);

        assertThat(films.get(0))
                .hasFieldOrPropertyWithValue("id", 1)
        ;

        assertThat(films.get(1))
                .hasFieldOrPropertyWithValue("id", 2)
        ;

        assertThat(films.get(2))
                .hasFieldOrPropertyWithValue("id", 3)
        ;

    }

    @Test
    public void testGetFilmByID() {

        Film film = filmStorage.getFilmByID(1);

        assertThat(film)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "testFilm1")
                .hasFieldOrPropertyWithValue("description", "test film description")
                .hasFieldOrPropertyWithValue("mpa", new Mpa(1, "G"))
                .hasFieldOrPropertyWithValue("duration", 100)
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2000, 2, 2))
        ;
    }

    @Test
    public void testAddLike() {
        boolean isLiked = filmStorage.addLike(1, 2);

        assertThat(isLiked)
                .isTrue();
    }

    @Test
    public void testRemoveLike() {
        boolean isLikeDeleted = filmStorage.addLike(1, 2);

        assertThat(isLikeDeleted)
                .isTrue();
    }

    @Test
    public void testGetPopularFilms() {
        List<Film> onePopularFilms = filmStorage.getPopularFilms(1);

        assertThat(onePopularFilms.get(0))
                .hasFieldOrPropertyWithValue("id", 2);

        List<Film> tenPopularFilms = filmStorage.getPopularFilms(10);

        assertThat(tenPopularFilms).hasSize(3);

        assertThat(tenPopularFilms.get(0))
                .hasFieldOrPropertyWithValue("id", 2)
        ;

        assertThat(tenPopularFilms.get(1))
                .hasFieldOrPropertyWithValue("id", 1)
        ;

        assertThat(tenPopularFilms.get(2))
                .hasFieldOrPropertyWithValue("id", 3)
        ;


    }

    @Test
    public void testFindAllGenres() {
        List<Genre> genres = genreStorage.findAllGenres();

        assertThat(genres).hasSize(6);
        assertThat(genres.get(0))
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "Комедия");

        assertThat(genres.get(5))
                .hasFieldOrPropertyWithValue("id", 6)
                .hasFieldOrPropertyWithValue("name", "Боевик");


    }

    @Test
    public void testGetGenreByID() {
        Genre genre = genreStorage.getGenreByID(1);
        assertThat(genre)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "Комедия");

    }

    @Test
    public void testFindAllMpas() {
        List<Mpa> mpa = mpaStorage.findAllMpas();

        assertThat(mpa).hasSize(5);
        assertThat(mpa.get(0))
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "G");

        assertThat(mpa.get(4))
                .hasFieldOrPropertyWithValue("id", 5)
                .hasFieldOrPropertyWithValue("name", "NC-17");

    }

    @Test
    public void testGetMpaByID() {
        Mpa mpa = mpaStorage.getMpaByID(1);

        assertThat(mpa)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "G");

    }


}

