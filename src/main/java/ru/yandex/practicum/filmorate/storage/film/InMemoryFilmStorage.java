package ru.yandex.practicum.filmorate.storage.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int filmID = 0;
    private final Map<Integer, Film> films = new HashMap<>();
    private final Logger log = LoggerFactory.getLogger(getClass());


    @Override
    public List<Film> getAllFilms() {
        log.info("Всего фильмов: {}", films.size());

        return new ArrayList<>(films.values());
    }

    @Override
    public Film addFilm(Film film) {
        int id = ++filmID;
        film.setId(id);
        films.put(id, film);
        log.info("Добавлен новый фильм {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм с таким ID не найден.");
        }
        films.put(film.getId(), film);
        log.info("Фильм обновлён {}", film);
        return film;
    }

    @Override
    public Film getFilmByID(int filmID) {
        return films.get(filmID);
    }
}
