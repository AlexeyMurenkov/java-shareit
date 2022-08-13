package ru.practicum.shareit.user.dto;

import lombok.Value;

@Value(staticConstructor = "of")
public class UserDto {
    long id;
    String name;
    String email;
}