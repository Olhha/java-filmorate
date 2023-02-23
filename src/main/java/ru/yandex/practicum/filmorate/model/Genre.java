package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Genre {
    private int id;
    private String name;

//    public Map<String, Object> toMap() {
//        Map<String, Object> values = new HashMap<>();
//        values.put("name", name);
//        values.put("genre_id", id);
//        return values;
//    }
}
