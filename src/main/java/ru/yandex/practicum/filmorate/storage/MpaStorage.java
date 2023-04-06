package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class MpaStorage {
    private final JdbcTemplate jdbcTemplate;
    private final Logger log = LoggerFactory.getLogger(getClass());


    @Autowired
    public MpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Mpa> findAllMpas() {
        String sqlQuery = "select mpa_id, name from rating_mpa";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMPA);
    }

    public Mpa mapRowToMPA(ResultSet resultSet, int i) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("mpa_id"))
                .name(resultSet.getString("name"))
                .build();
    }

    public Mpa getMpaByID(int mpaID) {
        Mpa mpa;
        String sqlQuery = "select mpa_id, name from rating_mpa where mpa_id = ?";

        try {
            mpa = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMPA, mpaID);
        } catch (EmptyResultDataAccessException e) {
            log.info(e.getMessage());
            throw new NotFoundException("Рейтинг MPA с id = " + mpaID + " не найден.");
        }
        return mpa;
    }
}
