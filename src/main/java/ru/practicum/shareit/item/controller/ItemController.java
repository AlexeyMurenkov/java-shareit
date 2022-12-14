package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemGetDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemGetDto getById(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                              @PathVariable @NotNull Long itemId) {
        log.debug("Запрос лот пользователя {} вещи по {}", userId, itemId);
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public Collection<ItemGetDto> getByUserId(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                              @RequestParam(defaultValue = "10") @PositiveOrZero int size) {
        log.debug("Запрос списка вещей пользователя с id={} постранично from={}, size={}", userId, from, size);
        return itemService.getItemsByUserId(userId, from, size);
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId, @RequestBody ItemDto itemDto) {
        log.debug("Запрос создания вещи {} от пользователя с id={}", itemDto, userId);
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId, @PathVariable @NotNull Long itemId,
                          @RequestBody ItemDto itemDto) {
        log.debug("Запрос обновления вещи {} от пользователя с id={}", itemDto, userId);
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchBySubstring(@RequestParam String text,
                                                 @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                 @RequestParam(defaultValue = "10") @PositiveOrZero int size) {
        log.debug("Запрос списка вещей по подстроке '{}' постранично from={}, size={}", text, from, size);
        return itemService.searchItemsBySubstring(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                    @PathVariable @NotNull Long itemId,
                                    @RequestBody @Valid @NotNull CommentDto commentDto) {
        log.debug("Запрос добавления комментария пользователя {} к вещи {}", userId, itemId);
        return itemService.createComment(commentDto, itemId, userId);
    }
}
