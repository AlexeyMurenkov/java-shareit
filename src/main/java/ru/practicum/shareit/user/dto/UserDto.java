package ru.practicum.shareit.user.dto;

import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Value(staticConstructor = "of")
public class UserDto {
    Long id;
    @NotBlank(message = "Имя пользователя должно быть заполнено")
    String name;
    @NotBlank
    @Email(message = "Некорректный email")
    String email;
}