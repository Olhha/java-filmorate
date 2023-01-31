package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;

import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


public class FilmValidationTests {
    private static final int FILM_DESCRIPTION_MAX_LENGTH = 200;
    FilmController fc = new FilmController();

    @Test
    void allFieldsCorrect() {
        Film film = new Film("Film1", LocalDate.of(1967, 3, 25), 100);
        film.setDescription("Description 1");

        fc.validateFilm(film);

        assertEquals("Film1", film.getName());
        assertEquals("Description 1", film.getDescription());
        assertEquals(LocalDate.of(1967, 3, 25), film.getReleaseDate());
        assertEquals(100, film.getDuration());

    }

    @Test
    void durationNegative() {
        Film film = new Film("Film1", LocalDate.of(1967, 3, 25), -100);
        film.setDescription("description1");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> fc.validateFilm(film));

        assertEquals(ValidationException.class, exception.getClass());
    }

    @Test
    void filmNameEmpty() {
        Film film = new Film("", LocalDate.of(1999, 6, 25), 100);
        film.setDescription("description1");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> fc.validateFilm(film));

        assertEquals(ValidationException.class, exception.getClass());
    }

    @Test
    void longDescription() {
        Film film = new Film("Film lond description", LocalDate.of(1999, 6, 25), 100);
        film.setDescription("u".repeat(FILM_DESCRIPTION_MAX_LENGTH + 1));

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> fc.validateFilm(film));

        assertEquals(ValidationException.class, exception.getClass());
    }

    @Test
    void dateBeforeFirstMovie() {
        Film film = new Film("Old old movie", LocalDate.of(1895, 11, 28), 100);
        film.setDescription("description1");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> fc.validateFilm(film));

        assertEquals(ValidationException.class, exception.getClass());
    }

}
