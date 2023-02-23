package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreController(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    @GetMapping
    public List<Genre> getAllGenres() {
        return genreStorage.findAllGenres();
    }

    @GetMapping("/{genreID}")
    public Genre getGenreByID(@PathVariable int genreID) {
        return genreStorage.getGenreByID(genreID);
    }
}
