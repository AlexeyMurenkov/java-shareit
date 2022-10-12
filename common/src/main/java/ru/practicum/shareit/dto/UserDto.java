package ru.practicum.shareit.dto;

import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Value(staticConstructor = "of")
public class UserDto {
    Long id;
    @NotBlank
    String name;
    @Email
    @NotBlank
    String email;
}