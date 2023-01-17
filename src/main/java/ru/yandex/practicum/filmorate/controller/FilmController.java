package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int filmID = 0;
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);

    @GetMapping
    public List<Film> findAllFilms() {
        log.info("Всего пользователей: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        validateFilm(film);
        int id = ++filmID;
        film.setId(id);
        films.put(id, film);
        log.info("Добавлен новый фильм {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        validateFilm(film);
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Фильм с таким ID не найден.");
        }
        films.put(film.getId(), film);
        log.info("Фильм обновлён {}", film);
        return film;
    }


    private void validateFilm(Film film) {
        final LocalDate FILM_FIRST_DAY = LocalDate.of(1895, 12, 28);

        if (film == null) {
            throw new ValidationException("Film can't be empty");
        }

        if (film.getName().isBlank()) {
            throw new ValidationException("Film Name can't be empty: " + film.getName());
        }

        if (film.getDescription().length() > 200) {
            throw new ValidationException("Film description can't be more than 200 chars: "
                    + film.getDescription());
        }

        if (film.getReleaseDate().isBefore(FILM_FIRST_DAY)) {
            throw new ValidationException("Film release date can't be earlier " +
                    "than the very first movie in the history: " + film.getReleaseDate());
        }

        if (film.getDuration() < 0) {
            throw new ValidationException("Film duration should be positive: " + film.getDuration());
        }
    }

}
