package ru.practicum.shareit.requests.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.ItemMapper.toItemsDto;
import static ru.practicum.shareit.user.dto.UserMapper.fromUserDto;
import static ru.practicum.shareit.user.dto.UserMapper.toUserDto;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest, Collection<Item> items) {
        return ItemRequestDto.of(
                itemRequest.getId(),
                itemRequest.getDescription(),
                toUserDto(itemRequest.getRequestor()),
                itemRequest.getCreated(),
                toItemsDto(items)
                );
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return toItemRequestDto(itemRequest, Collections.emptyList());
    }

    public static List<ItemRequestDto> toItemRequestsDto(Collection<ItemRequest> itemRequests,
                                                         Function<ItemRequest, Collection<Item>> getItems) {
        return itemRequests.stream()
                .map(itemRequest -> toItemRequestDto(itemRequest, getItems.apply(itemRequest)))
                .collect(Collectors.toList());
    }

    public static ItemRequest fromItemRequestDto(ItemRequestDto itemRequestDto) {
        return ItemRequest.of(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                fromUserDto(itemRequestDto.getRequestor()),
                itemRequestDto.getCreated()
        );
    }
}
