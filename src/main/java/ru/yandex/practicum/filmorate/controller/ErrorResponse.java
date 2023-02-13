package ru.yandex.practicum.filmorate.controller;

class ErrorResponse {
    private final String error;

    ErrorResponse(String error) {
        this.error = error;
    }

    String getError() {
        return error;
    }
}