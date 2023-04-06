package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    public GenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Genre> findAllGenres() {
        String sqlQuery = "select genre_id, name from genre";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    public Genre getGenreByID(int genreID) {
        Genre genre;
        String sqlQuery = "select genre_id, name from genre where genre_id = ?";

        try {
            genre = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, genreID);
        } catch (EmptyResultDataAccessException e) {
            log.info(e.getMessage());
            throw new NotFoundException("Жанр с id = " + genreID + " не найден.");
        }
        return genre;
    }

    public Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
