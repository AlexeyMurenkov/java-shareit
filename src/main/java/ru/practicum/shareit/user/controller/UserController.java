package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserController {

    private final UserService userService;

    @GetMapping()
    public Collection<UserDto> getAll() {
        log.debug("Запрос на получение списка всех пользователей");
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable long userId) {
        log.debug("Запрос пользователя по id={}", userId);
        return userService.getById(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody UserDto userDto) {
        log.debug("Запрос на создание пользователя {}", userDto);
        return userService.create(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable long userId, @RequestBody UserDto userDto) {
        log.debug("Запрос обновления {} пользователя id={}", userDto, userId);
        return userService.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void remove(@PathVariable long userId) {
        log.debug("Запрос на удаление пользователя id={}", userId);
        userService.remove(userId);
    }
}
