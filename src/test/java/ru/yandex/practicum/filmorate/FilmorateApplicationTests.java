package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.filmorate.service.FilmService.MAX_DESCRIPTION_LENGTH;

@SpringBootTest
@AutoConfigureMockMvc
class FilmorateApplicationTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    @Test
    void userCorrectFields() {
        User user = User.builder()
                .login("NewLogin")
                .name("NewName")
                .email("my@my.m")
                .birthday(LocalDate.of(2004, 2, 5))
                .build();

        String userString = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/users").contentType("application/json").content(userString))
                .andDo(h -> System.out.println(h.getResponse().getStatus()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("NewLogin"))
                .andExpect(jsonPath("$.name").value("NewName"))
                .andExpect(jsonPath("$.email").value("my@my.m"))
                .andExpect(jsonPath("$.birthday").value("2004-02-05"));
    }

    @SneakyThrows
    @Test
    void userInvalidEmail() {
        User user = User.builder()
                .login("login1")
                .name("User1")
                .email("m m@m")
                .birthday(LocalDate.of(1985, 2, 5))
                .build();

        checkGetting4xxClientError(objectMapper.writeValueAsString(user), "/users");
    }

    @SneakyThrows
    @Test
    void userEmptyLogin() {
        User user = User.builder()
                .login("")
                .name("User1")
                .email("my@my.m")
                .birthday(LocalDate.of(1985, 2, 5))
                .build();

        checkGetting4xxClientError(objectMapper.writeValueAsString(user), "/users");
    }

    @SneakyThrows
    @Test
    void userBlankLogin() {
        User user = User.builder()
                .login(" ")
                .name("User1")
                .email("my@my.m")
                .birthday(LocalDate.of(1985, 2, 5))
                .build();

        checkGetting4xxClientError(objectMapper.writeValueAsString(user), "/users");
    }

    @SneakyThrows
    @Test
    void userEmptyName() {
        User user = User.builder()
                .login("NewLogin")
                .name("")
                .email("my@my.m")
                .birthday(LocalDate.of(1985, 2, 5))
                .build();

        String userString = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/users").contentType("application/json").content(userString))
                .andDo(h -> System.out.println(h.getResponse().getStatus()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NewLogin"));
    }

    @SneakyThrows
    @Test
    void userBirthdayInFuture() {
        User user = User.builder()
                .login("NewLogin")
                .name("NewName")
                .email("my@my.m")
                .birthday(LocalDate.of(2985, 2, 5))
                .build();

        checkGetting4xxClientError(objectMapper.writeValueAsString(user), "/users");
    }

    @SneakyThrows
    @Test
    void filmCorrectFields() {
        Film film = Film.builder()
                .name("New Film Caption")
                .description("Very interesting film")
                .releaseDate(LocalDate.of(2004, 6, 5))
                .duration(30)
                .mpa(new Mpa(1, "G"))
                .build();

        String userString = objectMapper.writeValueAsString(film);

        mockMvc.perform(post("/films").contentType("application/json").content(userString))
                .andDo(h -> System.out.println(h.getResponse().getStatus()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("New Film Caption"))
                .andExpect(jsonPath("$.description").value("Very interesting film"))
                .andExpect(jsonPath("$.duration").value("30"))
                .andExpect(jsonPath("$.releaseDate").value("2004-06-05"))

        ;
    }

    @SneakyThrows
    @Test
    void filmEmptyName() {
        Film film = Film.builder()
                .name("")
                .description("Very interesting film")
                .releaseDate(LocalDate.of(2004, 6, 5))
                .duration(30)
                .build();

        checkGetting4xxClientError(objectMapper.writeValueAsString(film), "/films");
    }

    @SneakyThrows
    @Test
    void filmLongDescription() {
        Film film = Film.builder()
                .name("")
                .description("u".repeat(MAX_DESCRIPTION_LENGTH + 1))
                .releaseDate(LocalDate.of(2004, 6, 5))
                .duration(30)
                .build();

        checkGetting4xxClientError(objectMapper.writeValueAsString(film), "/films");
    }

    @SneakyThrows
    @Test
    void filmDateBeforeFirstMovie() {
        Film film = Film.builder()
                .name("Old film")
                .description("Old old movie")
                .releaseDate(LocalDate.of(1895, 11, 28))
                .duration(30)
                .build();

        checkGetting4xxClientError(objectMapper.writeValueAsString(film), "/films");
    }

    @SneakyThrows
    @Test
    void filmIncorrectDuration() {
        Film film = Film.builder()
                .name("FilmName")
                .description("Very short film")
                .releaseDate(LocalDate.of(2006, 11, 28))
                .duration(0)
                .build();

        checkGetting4xxClientError(objectMapper.writeValueAsString(film), "/films");
    }

    private void checkGetting4xxClientError(String objectMapper, String urlTemplate) throws Exception {

        mockMvc.perform(post(urlTemplate).contentType("application/json").content(objectMapper))
                .andDo(h -> System.out.println(h.getResponse().getStatus()))
                .andExpect(status().is4xxClientError());
    }
}