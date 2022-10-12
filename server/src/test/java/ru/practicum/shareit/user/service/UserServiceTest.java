package ru.practicum.shareit.user.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class UserServiceTest {
    UserDto testUserDto = UserDto.of(1L, "Test user 1", "user1@email.test");
    List<UserDto> testUsersDto = List.of(
            testUserDto,
            UserDto.of(2L, "Test user 2", "user2@email.test"),
            UserDto.of(3L, "Test user 3", "user3@email.test")
    );

    User testUser = User.of(1L, "Test user 1", "user1@email.test");
    List<User> testUsers = List.of(
            testUser,
            User.of(2L, "Test user 2", "user2@email.test"),
            User.of(3L, "Test user 3", "user3@email.test")
    );

    UserRepository userRepository = mock(UserRepository.class);
    UserService userService = new UserServiceImpl(userRepository);

    @Test
    void getAllUsers() {
    userService.getAllUsers();
        when(userRepository.findAll()).thenReturn(testUsers);

        final List<UserDto> usersDto = userService.getAllUsers();
        assertNotNull(usersDto, "Список пользователей не возвращается");
        assertIterableEquals(testUsersDto, usersDto, "Возвращается неверный список пользователей");
    }

    @Test
    void getUserById() {
        when(userRepository.getReferenceById(anyLong())).thenReturn(testUser);
        when(userRepository.existsById(anyLong())).thenReturn(true);

        final UserDto userDto = userService.getUserById(1L);
        assertNotNull(userDto, "Не возвращается пользователь по id");
        assertEquals(testUserDto, userDto, "Возвращается неверный пользователь");
    }

    @Test
    void getUserByInvalidId() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        final Throwable e = assertThrows(NotFoundException.class, () -> userService.getUserById(1L),
                "При неверном id пользователя не возникает исключения");
        assertEquals("Пользователь с id=1 не найден", e.getMessage(), "Неверное сообщение об ошибке");
    }

    @Test
    void createUser() {
        when(userRepository.save(any())).thenReturn(testUser);
        when(userRepository.getReferenceById(any())).thenReturn(testUser);

        final UserDto userDto = userService.createUser(testUserDto);
        assertNotNull(userDto, "Не возвращается пользователь");
        assertEquals(testUserDto, userDto, "Возвращается неверный пользователь");
    }

    @Test
    void updateUser() {
        when(userRepository.save(any())).thenReturn(testUser);
        when(userRepository.getReferenceById(any())).thenReturn(testUser);
        when(userRepository.existsById(anyLong())).thenReturn(true);

        final UserDto userDto = userService.updateUser(1L, testUserDto);
        assertNotNull(userDto, "Не возвращается пользователь");
        assertEquals(testUserDto, userDto, "Возвращается неверный пользователь");
    }

    @Test
    void removeUser() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.removeUser(1L);
    }
}