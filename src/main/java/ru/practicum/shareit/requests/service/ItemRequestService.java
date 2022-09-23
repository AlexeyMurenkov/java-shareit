package ru.practicum.shareit.requests.service;

import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDto> getItemRequestsByRequestorId(Long userId);

    List<ItemRequestDto> getItemRequestsByNotRequestorId(Long userId, int from, int size);

    ItemRequestDto getRequestById(Long userId, Long requestId);
}
