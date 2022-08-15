package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable long itemId) {
        log.debug("Запрос вещи по id={}", itemId);
        return itemService.getById(itemId);
    }

    @GetMapping
    public Collection<ItemDto> getByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("Запрос списка вещей пользователя с id={}", userId);
        return itemService.getByUserId(userId);
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody ItemDto itemDto) {
        log.debug("Запрос создания вещи {} от пользователя с id={}", itemDto, userId);
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                          @RequestBody ItemDto itemDto) {
        log.debug("Запрос обновления вещи {} от пользователя с id={}", itemDto, userId);
        return itemService.update(itemDto, itemId, userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchBySubstring(@RequestParam String text) {
        log.debug("Запрос списка вещей по подстроке '{}'", text);
        return itemService.searchBySubstring(text);
    }
}
