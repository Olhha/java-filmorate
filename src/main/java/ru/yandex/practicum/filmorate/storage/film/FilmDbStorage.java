package ru.yandex.practicum.filmorate.storage.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
    }

    @Override
    public Film addFilm(Film film) {

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        int filmID = simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue();
        film.setId(filmID);
        if (!film.getGenres().isEmpty()) {
            insertGenresForFilm(film);
        }

        log.info("Добавлен новый фильм {}", film);

        return film;
    }

    @Override
    public Film updateFilm(Film film) {

        String sqlQuery = "update films set " +
                "name = ?, description = ?, release_date = ?, " +
                "duration = ?, mpa_id =? where film_id = ?";

        jdbcTemplate.update(sqlQuery
                , film.getName()
                , film.getDescription()
                , film.getReleaseDate()
                , film.getDuration()
                , film.getMpa().getId()
                , film.getId());

        deleteAllGenresForFilm(film.getId());
        insertGenresForFilm(film);

        return getFilmByID(film.getId());
    }

    private void deleteAllGenresForFilm(int filmID) {
        String sqlQuery = "delete from FILM_GENRE where FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, filmID);
    }

    private void insertGenresForFilm(Film film) {
        String sqlQueryGenre = "insert into FILM_GENRE (GENRE_ID, FILM_ID) " +
                "VALUES (?, ?)";

        film.getGenres().stream().distinct().collect(Collectors.toList())
                .forEach((g) -> jdbcTemplate.update(sqlQueryGenre
                        , g.getId()
                        , film.getId()));
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "select f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, " +
                "f.DURATION, f.MPA_ID, m.NAME mpa_name from FILMS f " +
                "left join RATING_MPA m on f.MPA_ID = m.MPA_ID ";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
        Map<Integer, List<Genre>> genres = getAllGenres();

        films.forEach(f -> setGenresOnFilm(f, genres.get(f.getId())));

        return films;
    }

    @Override
    public Film getFilmByID(int filmID) {
        String sqlQuery = "select f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, " +
                "f.DURATION, f.MPA_ID, m.NAME mpa_name from FILMS f " +
                "left join RATING_MPA m on f.MPA_ID = m.MPA_ID " +
                "where f.FILM_ID = ?";

        Film film;

        try {
            film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, filmID);
        } catch (EmptyResultDataAccessException e) {
            log.info(e.getMessage());
            return null;
        }

        String sqlQueryGenere = "select fg.genre_id, g.NAME from FILM_GENRE fg, " +
                "GENRE g where g.GENRE_ID = fg.GENRE_ID and fg.film_id = ?";

        jdbcTemplate.query(sqlQueryGenere, genreStorage::mapRowToGenre, film.getId())
                .forEach(film::addGenre);

        return film;
    }

    @Override
    public boolean addLike(int filmID, int userID) {
        if (!checkIfLikeExists(filmID, userID)) {
            String sqlQuery = "insert into film_likes (film_id, user_id ) values (?, ?) ";
            return jdbcTemplate.update(sqlQuery, filmID, userID) > 0;
        }
        return false;
    }

    @Override
    public boolean removeLike(int filmID, int userID) {
        if (checkIfLikeExists(filmID, userID)) {
            String sqlQuery = "delete from film_likes where film_id = ? and user_id = ?";
            return jdbcTemplate.update(sqlQuery, filmID, userID) > 0;
        }
        return false;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sqlQuery = "select f.film_id, " +
                "       f.mpa_id, " +
                "       f.name, " +
                "       f.description, " +
                "       f.release_date, " +
                "       f.duration, " +
                "       m.NAME mpa_name " +
                "from FILMS f " +
                "left join RATING_MPA m on f.MPA_ID = m.MPA_ID " +
                "left join film_likes l on f.film_id = l.film_id " +
                "group by f.film_id " +
                "order by count(l.user_id) DESC " +
                "limit  ? ";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);

        Map<Integer, List<Genre>> genres = getAllGenres();

        films.forEach(f -> setGenresOnFilm(f, genres.get(f.getId())));

        return films;

    }

    private boolean checkIfLikeExists(int filmID, int userID) {
        String sqlQuery = "select * from film_likes where film_id = ? and user_id = ?";
        return jdbcTemplate.query(sqlQuery, ResultSet::next, filmID, userID);
    }

    private Map<Integer, List<Genre>> getAllGenres() {
        final Map<Integer, List<Genre>> genres = new HashMap<>();

        jdbcTemplate.query("select fg.film_id, fg.genre_id, g.name from film_genre fg, genre g " +
                "where fg.genre_id = g.genre_id", (ResultSet rs) -> {

            int film_id = rs.getInt("film_id");
            Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("name"));
            genres.computeIfAbsent(film_id, k -> new ArrayList<>()).add(genre);
        });
        return genres;
    }

    private Film setGenresOnFilm(Film film, List<Genre> genres) {
        if (genres != null) {
            genres.forEach(film::addGenre);
        }
        return film;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("film_id"))
                .duration(resultSet.getInt("duration"))
                .description(resultSet.getString("description"))
                .name(resultSet.getString("name"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .mpa(new Mpa(resultSet.getInt("mpa_id")
                        , resultSet.getString("mpa_name")))
                .build();
    }
}
