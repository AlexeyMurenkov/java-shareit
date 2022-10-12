package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@Validated
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserController {

    private final UserClient userClient;

    @GetMapping()
    public ResponseEntity<Object> getAll() {
        log.debug("Запрос на получение списка всех пользователей");
        return userClient.getAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getById(@PathVariable @NotNull Long userId) {
        log.debug("Запрос пользователя по id={}", userId);
        return userClient.getById(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestBody @Valid UserDto userDto) {
        log.debug("Запрос на создание пользователя {}", userDto);
        return userClient.create(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable @NotNull Long userId, @RequestBody UserDto userDto) {
        log.debug("Запрос обновления {} пользователя id={}", userDto, userId);
        return userClient.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void remove(@PathVariable @NotNull Long userId) {
        log.debug("Запрос на удаление пользователя id={}", userId);
        userClient.remove(userId);
    }
}
