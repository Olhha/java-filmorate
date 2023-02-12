package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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

    private final Set<Integer> likesFromUsers = new HashSet<>();

    public boolean addLike(int userID) {
        return likesFromUsers.add(userID);
    }

    public boolean removeLike(int userID) {
        return likesFromUsers.remove(userID);
    }
}
