package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemGetDto;

import java.util.List;

public interface ItemService {
    List<ItemGetDto> getItemsByUserId(Long userId, int from, int size);

    ItemGetDto getItemById(Long itemId, Long userId);

    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId);

    List<ItemDto> searchItemsBySubstring(String substring, int from, int size);

    CommentDto createComment(CommentDto commentDto, Long itemId, Long userId);
}

