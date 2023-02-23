package ru.yandex.practicum.filmorate.storage.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component("inMemoryFilmStorage")
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
        if (films.put(film.getId(), film) == null) {
            return null;
        }
        log.info("Фильм обновлён {}", film);
        return film;
    }

    @Override
    public Film getFilmByID(int filmID) {
        return films.get(filmID);
    }

    @Override
    public boolean addLike(int filmID, int userID) {
        return false;
    }

    @Override
    public boolean removeLike(int filmID, int userID) {
        return false;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        Comparator<Film> filmsComparator =
                Comparator.comparingInt(f -> f.getLikesFromUsers().size());

        return getAllFilms().stream().sorted(
                        filmsComparator.reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

}
