package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.CommentDto;
import ru.practicum.shareit.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                  @PathVariable @NotNull Long itemId) {
        log.debug("Запрос от пользователя {} вещи по {}", userId, itemId);
        return itemClient.getById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getByUserId(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                              @RequestParam(defaultValue = "10") @PositiveOrZero int size) {
        log.debug("Запрос списка вещей пользователя с id={} постранично from={}, size={}", userId, from, size);
        return itemClient.getByUserId(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                         @RequestBody @Valid ItemDto itemDto) {
        log.debug("Запрос создания вещи {} от пользователя с id={}", itemDto, userId);
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                         @PathVariable @NotNull Long itemId,
                                         @RequestBody ItemDto itemDto) {
        log.debug("Запрос обновления вещи {} от пользователя с id={}", itemDto, userId);
        return itemClient.update(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchBySubstring(@RequestParam String text,
                                                 @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                 @RequestParam(defaultValue = "10") @PositiveOrZero int size) {
        log.debug("Запрос списка вещей по подстроке '{}' постранично from={}, size={}", text, from, size);
        return itemClient.searchBySubstring(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                    @PathVariable @NotNull Long itemId,
                                    @RequestBody @Valid @NotNull CommentDto commentDto) {
        log.debug("Запрос добавления комментария пользователя {} к вещи {}", userId, itemId);
        return itemClient.createComment(userId, itemId, commentDto);
    }
}
