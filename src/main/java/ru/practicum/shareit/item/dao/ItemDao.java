package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemDao {
    Collection<Item> getByUserId(long userId);

    Item getById(long itemId);

    Item create(Item item);

    Item update(Item item);

    Collection<Item> searchBySubstring(String substring);
}
