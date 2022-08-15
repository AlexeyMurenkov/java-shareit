package ru.practicum.shareit.common;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.common.exceptoins.ValidationException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ModelValidator<T> {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public void apply(T t) {
        final Set<ConstraintViolation<T>> violations = validator.validate(t);

        if (!violations.isEmpty()) {
            final Map<String, String> violationsMap = violations.stream()
                    .collect(Collectors.toMap(v -> v.getPropertyPath().toString(), ConstraintViolation::getMessage));
            throw new ValidationException(violationsMap);
        }
    }
}
