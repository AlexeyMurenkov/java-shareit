package ru.practicum.shareit.common.exceptoins;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}