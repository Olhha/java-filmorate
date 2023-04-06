package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;



@Builder
@Value
@AllArgsConstructor
public class Genre {
    int id;
    String name;
}
