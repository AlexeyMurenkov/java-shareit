package ru.practicum.shareit.common.exceptoins;

import java.util.Map;

public class ValidationException extends RuntimeException {

    private final Map<String, String> violations;

    public ValidationException(String message) {
        super("Ошибка валидации");
        this.violations = Map.of("error", message);
    }

    public ValidationException(Map<String, String> violations) {
        super("Ошибка валидации");
        this.violations = violations;
    }

    public Map<String, String> getViolations() {
        return violations;
    }
}
