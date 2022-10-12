package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return UserDto.of(user.getId(), user.getName(), user.getEmail());
    }

    public static List<UserDto> toUsersDto(List<User> users) {
        return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    public static User fromUserDto(UserDto userDto) {
        return User.of(userDto.getId(), userDto.getName(), userDto.getEmail());
    }
}