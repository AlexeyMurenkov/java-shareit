package ru.practicum.shareit.common.exceptoins;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
