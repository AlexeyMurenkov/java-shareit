package ru.practicum.shareit.item.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.CommentDto;
import ru.practicum.shareit.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemGetDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemController {

    ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemGetDto getById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.debug("Запрос лот пользователя {} вещи по {}", userId, itemId);
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public Collection<ItemGetDto> getByUserId(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam int from,
                                              @RequestParam int size) {
        log.debug("Запрос списка вещей пользователя с id={} постранично from={}, size={}", userId, from, size);
        return itemService.getItemsByUserId(userId, from, size);
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto) {
        log.debug("Запрос создания вещи {} от пользователя с id={}", itemDto, userId);
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                          @RequestBody ItemDto itemDto) {
        log.debug("Запрос обновления вещи {} от пользователя с id={}", itemDto, userId);
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchBySubstring(@RequestParam String text, @RequestParam int from,
                                                 @RequestParam int size) {
        log.debug("Запрос списка вещей по подстроке '{}' постранично from={}, size={}", text, from, size);
        return itemService.searchItemsBySubstring(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long itemId,
                                    @RequestBody CommentDto commentDto) {
        log.debug("Запрос добавления комментария пользователя {} к вещи {}", userId, itemId);
        return itemService.createComment(commentDto, itemId, userId);
    }
}
