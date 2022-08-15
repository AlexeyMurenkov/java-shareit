package ru.practicum.shareit.common.exceptoins;

import java.util.Map;

public class ValidationException extends RuntimeException {

    private final Map<String, String> violations;

    public ValidationException(Map<String, String> violations) {
        super("Ошибка валидации");
        this.violations = violations;
    }

    public Map<String, String> getViolations() {
        return violations;
    }
}
