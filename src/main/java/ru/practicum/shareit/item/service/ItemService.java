package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    Collection<ItemDto> getByUserId(long userId);

    ItemDto getById(long itemId);

    ItemDto create(ItemDto itemDto, long userId);

    ItemDto update(ItemDto itemDto, long itemId, long userId);

    Collection<ItemDto> searchBySubstring(String substring);
}

