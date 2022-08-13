package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exceptoins.ForbiddenException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.ItemMapper.fromItemDto;
import static ru.practicum.shareit.item.dto.ItemMapper.toItemDto;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemStorage;
    private final UserDao userStorage;

    private Item patchItem(Item recipient, Item donor) {
        return Item.of(
                recipient.getId(),
                Optional.ofNullable(donor.getName()).orElse(recipient.getName()),
                Optional.ofNullable(donor.getDescription()).orElse(recipient.getDescription()),
                Optional.ofNullable(donor.getAvailable()).orElse(recipient.getAvailable()),
                recipient.getOwnerId(),
                recipient.getRequest()
        );
    }
    @Override
    public Collection<ItemDto> getByUserId(long userId) {
        return itemStorage.getByUserId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(long itemId) {
        return toItemDto(itemStorage.getById(itemId));
    }

    @Override
    public ItemDto create(ItemDto itemDto, long userId) {
        userStorage.getById(userId);
        final Item item = fromItemDto(itemDto, 0, userId);
        final Item createdItem = itemStorage.create(item);

        return toItemDto(createdItem);
    }

    @Override
    public ItemDto update(ItemDto itemDto, long itemId, long userId) {
        final User owner = userStorage.getById(userId);
        final Item item = itemStorage.getById(itemId);

        if (item.getOwnerId() == owner.getId()) {
            final Item donorItem = fromItemDto(itemDto, itemId, userId);
            return toItemDto(itemStorage.update(patchItem(item, donorItem)));
        }

        throw new ForbiddenException(String.format("Попытка пользователя с id=%s обновить вещь пользователя id=%s",
                userId, item.getOwnerId()));
    }

    @Override
    public Collection<ItemDto> searchBySubstring(String substring) {
        return itemStorage.searchBySubstring(substring).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
