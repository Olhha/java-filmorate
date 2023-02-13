package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);

    boolean updateFilm(Film film);

    List<Film> getAllFilms();

    Film getFilmByID(int filmID);
}