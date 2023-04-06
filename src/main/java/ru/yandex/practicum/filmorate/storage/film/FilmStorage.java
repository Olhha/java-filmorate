package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getAllFilms();

    Film getFilmByID(int filmID);

    boolean addLike(int filmID, int userID);

    boolean removeLike(int filmID, int userID);

    List<Film> getPopularFilms(int count);
}
