package ru.practicum.shareit.request.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemRequestController {

    ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody ItemRequestDto itemRequestDto) {
        log.debug("Создание пользователем {} запроса вещи", userId);
        return itemRequestService.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getByRequestor(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Получение пользователем {} списка своих запросов", userId);
        return itemRequestService.getItemRequestsByRequestorId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getByNotRequestor(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam int from, @RequestParam int size) {
        log.debug("Получение пользователем {} списка всех запросов постранично from={}, size={}", userId, from, size);
        return itemRequestService.getItemRequestsByNotRequestorId(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        log.debug("Получение пользователем {} запроса по id={}", userId, requestId);
        return itemRequestService.getRequestById(userId, requestId);
    }
}
