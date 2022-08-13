package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> getAll();

    UserDto getById(long id);

    UserDto create(UserDto userDto);

    UserDto update(long userId, UserDto userDto);

    void remove(long id);
}
