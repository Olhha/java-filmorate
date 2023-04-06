package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

import static ru.yandex.practicum.filmorate.service.FilmService.MAX_DESCRIPTION_LENGTH;

@Data
@Builder
public class Film {
    private int id;
    @NotBlank
    private final String name;
    @Size(max = MAX_DESCRIPTION_LENGTH)
    private String description;
    @NotNull
    private final LocalDate releaseDate;
    @Positive
    private final int duration;
    private final Mpa mpa;
    private final List<Genre> genres = new ArrayList<>();
    private final Set<Integer> likesFromUsers = new HashSet<>();

    public boolean addLike(int userID) {
        return likesFromUsers.add(userID);
    }

    public boolean removeLike(int userID) {
        return likesFromUsers.remove(userID);
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("description", description);
        values.put("release_date", releaseDate);
        values.put("duration", duration);
        values.put("mpa_id", mpa.getId());
        return values;
    }
}
