package ru.practicum.shareit.common.exceptoins;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
