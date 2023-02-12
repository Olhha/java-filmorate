package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> findAllFilms() {
        return filmService.findAllFilms();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

@GetMapping("/{filmID}")
public Film getFilmByID(@PathVariable int filmID){
        return filmService.getFilmByID(filmID);
}

    @PutMapping("/{filmID}/like/{userID}")
    public Film addLike(@PathVariable int filmID, @PathVariable int userID) {
        return filmService.addLike(filmID, userID);
    }

    @DeleteMapping("/{filmID}/like/{userID}")
    public Film deleteLike(@PathVariable int filmID, @PathVariable int userID){
        return filmService.deleteLike(filmID,userID);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(
            @RequestParam (required = false, defaultValue = "10") int count){
        return filmService.getPopularFilms(count);
    }
}
