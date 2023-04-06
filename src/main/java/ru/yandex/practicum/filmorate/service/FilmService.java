package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.List;

@Service
public class FilmService {
    public static final int MAX_DESCRIPTION_LENGTH = 200;
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public List<Film> findAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addFilm(Film film) {
        validateFilm(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        getFilmByID(film.getId());
        validateFilm(film);

        return filmStorage.updateFilm(film);
    }

    public void validateFilm(Film film) {
        final LocalDate FILM_FIRST_DAY = LocalDate.of(1895, 12, 28);

        if (film.getReleaseDate().isBefore(FILM_FIRST_DAY)) {
            throw new ValidationException("Film release date can't be earlier " +
                    "than the very first movie in the history: " + film.getReleaseDate());
        }

    }

    public Film addLike(int filmID, int userID) {
        userService.checkIfUserExists(userID);

        Film film = getFilmByID(filmID);

        if (!filmStorage.addLike(filmID, userID)) {
            throw new AlreadyExistException(String.format("Пользователь id=%d уже ставил " +
                    "лайк фильму id=%d.", userID, filmID));
        }

        return film;
    }

    public Film getFilmByID(int filmID) {
        Film film = filmStorage.getFilmByID(filmID);
        if (film == null) {
            throw new NotFoundException("Фильма id = " + filmID + " пока нет в списке.");
        }
        return film;
    }

    public Film deleteLike(int filmID, int userID) {
        userService.checkIfUserExists(userID);
        Film film = getFilmByID(filmID);

        if (!filmStorage.removeLike(filmID, userID)) {
            throw new NotFoundException(String.format("У фильма id=%d нет лайка " +
                    "от пользователя id=%d.", filmID, userID));
        }

        return film;
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }
}
