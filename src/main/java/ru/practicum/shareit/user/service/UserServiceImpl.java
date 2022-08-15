package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.dto.UserMapper.fromUserDto;
import static ru.practicum.shareit.user.dto.UserMapper.toUserDto;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpl implements UserService {
    private final UserDao userStorage;

    private User patchUser(User recipient, User donor) {
        return User.of(
                recipient.getId(),
                Optional.ofNullable(donor.getName()).orElse(recipient.getName()),
                Optional.ofNullable(donor.getEmail()).orElse(recipient.getEmail())
        );
    }

    @Override
    public Collection<UserDto> getAll() {
        return userStorage.getAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getById(long id) {
        return toUserDto(userStorage.getById(id));
    }

    @Override
    public UserDto create(UserDto userDto) {
        final User user = fromUserDto(userDto, 0);
        final User createdUser = userStorage.create(user);
        return toUserDto(userStorage.getById(createdUser.getId()));
    }

    @Override
    public UserDto update(long userId, UserDto userDto) {
        final User donorUser = fromUserDto(userDto, userId);
        final User user = userStorage.getById(userId);

        return toUserDto(userStorage.update(patchUser(user, donorUser)));
    }

    @Override
    public void remove(long id) {
        userStorage.remove(id);
    }
}
