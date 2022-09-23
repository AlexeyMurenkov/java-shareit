package ru.practicum.shareit.requests.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exceptoins.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.requests.dto.ItemRequestMapper.*;
import static ru.practicum.shareit.user.dto.UserMapper.toUserDto;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemRequestServiceImpl implements ItemRequestService {

    ItemRequestRepository itemRequestRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;

    @Override
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        final User requestor = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Запрос от несуществующего пользователя")
        );

        final UserDto requestorDto = toUserDto(requestor);

        final ItemRequestDto createdItemRequestDto = itemRequestDto
                .withRequestor(requestorDto)
                .withCreated(LocalDateTime.now());

        final ItemRequest itemRequest = fromItemRequestDto(createdItemRequestDto);

        return toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getItemRequestsByRequestorId(Long userId) {
        final User requestor = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Запрос от несуществующего пользователя")
        );

        return toItemRequestsDto(itemRequestRepository.findAllByRequestorOrderByCreated(requestor),
                itemRepository::findAllByRequest);
    }

    @Override
    public List<ItemRequestDto> getItemRequestsByNotRequestorId(Long userId, int from, int size) {
        final User requestor = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Запрос от несуществующего пользователя")
        );

        final Pageable pageble = PageRequest.of(from, size);

        return toItemRequestsDto(itemRequestRepository.findAllByNotRequestorOrderByCreated(requestor, pageble)
                .getContent(), itemRepository::findAllByRequest);
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Запрос от несуществующего пользователя");
        }

        final ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Обращение к несуществующему запросу")
        );

        final List<Item> items = itemRepository.findAllByRequest(itemRequest);

        return toItemRequestDto(itemRequest, items);
    }
}
