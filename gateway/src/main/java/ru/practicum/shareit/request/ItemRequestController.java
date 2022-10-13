package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemRequestController {

    ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.debug("Создание пользователем {} запроса вещи", userId);
        return itemRequestClient.create(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getByRequestor(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Получение пользователем {} списка своих запросов", userId);
        return itemRequestClient.getByRequestor(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getByNotRequestor(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                  @RequestParam(defaultValue = "10") @PositiveOrZero int size) {
        log.debug("Получение пользователем {} списка всех запросов постранично from={}, size={}", userId, from, size);
        return itemRequestClient.getByNotRequestor(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        log.debug("Получение пользователем {} запроса по id={}", userId, requestId);
        return itemRequestClient.getById(userId, requestId);
    }
}
