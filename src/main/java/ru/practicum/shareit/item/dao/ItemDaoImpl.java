package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.common.TempStorage;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.stream.Collectors;

@Repository
public class ItemDaoImpl implements ItemDao {

    private final TempStorage<Item> itemStorage = new TempStorage<>();

    @Override
    public Collection<Item> getByUserId(long userId) {
        return itemStorage.getAll().stream().filter(i -> i.getOwnerId() == userId).collect(Collectors.toList());
    }

    @Override
    public Item getById(long itemId) {
        return itemStorage.getById(itemId);
    }

    @Override
    public Item create(Item item) {
        return itemStorage.create(
                Item.of(
                        itemStorage.getNext(),
                        item.getName(),
                        item.getDescription(),
                        item.getAvailable(),
                        item.getOwnerId(),
                        item.getRequest()
                )
        );
    }

    @Override
    public Item update(Item item) {
        return itemStorage.update(item.getId(), item);
    }

    @Override
    public Collection<Item> searchBySubstring(String substring) {
        if (substring.isBlank()) return Collections.emptyList();
        final String forSearch = substring.toLowerCase(Locale.ROOT);
        return itemStorage.getAll().stream()
                .filter(item -> item.getAvailable() && (item.getName().toLowerCase(Locale.ROOT).contains(forSearch)
                        || item.getDescription().toLowerCase(Locale.ROOT).contains(forSearch)))
                .collect(Collectors.toList());
    }
}
