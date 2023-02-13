package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    public static final int MAX_DESCRIPTION_LENGTH = 200;
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
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
        validateFilm(film);
        if (!filmStorage.updateFilm(film)) {
            throw new NotFoundException("Фильм с таким ID не найден.");
        }
        return film;
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

        if (!film.addLike(userID)) {
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

        if (!film.removeLike(userID)) {
            throw new NotFoundException(String.format("У фильма id=%d нет лайка " +
                    "от пользователя id=%d.", filmID, userID));
        }

        return film;
    }

    public List<Film> getPopularFilms(int count) {
        Comparator<Film> filmsComparator =
                Comparator.comparingInt(f -> f.getLikesFromUsers().size());

        return filmStorage.getAllFilms().stream().sorted(
                        filmsComparator.reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
