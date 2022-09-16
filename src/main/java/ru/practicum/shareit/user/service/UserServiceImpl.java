package ru.practicum.shareit.user.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.ModelValidator;
import ru.practicum.shareit.common.exceptoins.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.user.dto.UserMapper.*;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {
    UserRepository userStorage;
    ModelValidator<UserDto> modelValidator;

    private UserDto patchUser(UserDto recipient, UserDto donor) {
        return UserDto.of(
                recipient.getId(),
                Optional.ofNullable(donor.getName()).orElse(recipient.getName()),
                Optional.ofNullable(donor.getEmail()).orElse(recipient.getEmail())
        );
    }

    private void checkUserId(Long id) throws NotFoundException {
        if (!userStorage.existsById(id)) {
            throw new NotFoundException(String.format("Пользователь с id=%s не найден", id));
        }

    }

    @Override
    public List<UserDto> getAllUsers() {
        return toUsersDto(userStorage.findAll());
    }

    @Override
    public UserDto getUserById(Long id) {
        checkUserId(id);
        return toUserDto(userStorage.getReferenceById(id));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        modelValidator.apply(userDto);
        final User user = fromUserDto(userDto);
        final User createdUser = userStorage.save(user);
        return toUserDto(userStorage.getReferenceById(createdUser.getId()));
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        checkUserId(id);
        final UserDto recipient = toUserDto(userStorage.getReferenceById(id));
        final UserDto patched = patchUser(recipient, userDto);
        modelValidator.apply(patched);

        return toUserDto(userStorage.save(fromUserDto(patched)));
    }

    @Override
    public void removeUser(Long id) {
        checkUserId(id);
        userStorage.deleteById(id);
    }
}
