package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class UserMapperTest {

    User testUser = User.of(1L, "Test user", "test@user.email");
    UserDto testUserDto = UserDto.of(1L, "Test user", "test@user.email");

    @Test
    void toUserDto() {
        final UserDto userDto = UserMapper.toUserDto(testUser);
        assertNotNull(userDto, "Возвращается пустое DTO пользователя");
        assertEquals(testUserDto, userDto, "Возвращается неверное DTO пользователя");
    }

    @Test
    void toUsersDto() {
        final List<UserDto> usersDto = UserMapper.toUsersDto(List.of(testUser));
        assertNotNull(usersDto, "Возвращается пустой список DTO пользователей");
        assertIterableEquals(List.of(testUserDto), usersDto, "Возвращается неверный список DTO пользователей");
    }

    @Test
    void fromUserDto() {
        final User user = UserMapper.fromUserDto(testUserDto);
        assertNotNull(user, "Возвращается пустой значение пользователя");
        assertEquals(testUser.getId(), user.getId(), "Возвращается неверный id пользователя");
        assertEquals(testUser.getName(), user.getName(), "Возвращается неверное имя пользователя");
        assertEquals(testUser.getEmail(), user.getEmail(), "Возвращается неверная почта пользователя");
    }

    @Test
    void defaultConstructor() {
        final UserMapper userMapper = new UserMapper();
        assertNotNull(userMapper, "Объект маппера пользователей не создается");
        assertInstanceOf(UserMapper.class, userMapper, "Создается объект неверного класса");
    }
}