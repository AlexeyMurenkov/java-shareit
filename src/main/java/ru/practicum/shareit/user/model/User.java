package ru.practicum.shareit.user.model;

import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Value(staticConstructor = "of")
public class User {
    long id;
    @NotNull(message = "Имя пользователя не заполнено")
    String name;
    @NotNull(message = "email пользователя не заполнен")
    @Email(message = "Невалидный email пользователя")
    String email;
}