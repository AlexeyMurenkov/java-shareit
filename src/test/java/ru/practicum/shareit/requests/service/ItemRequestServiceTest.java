package ru.practicum.shareit.requests.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.common.exceptoins.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class ItemRequestServiceTest {

    static LocalDateTime NOW_DATE_TIME = LocalDateTime.now();

    ItemRepository itemRepository = mock(ItemRepository.class);
    ItemRequestRepository itemRequestRepository = mock(ItemRequestRepository.class);
    UserRepository userRepository = mock(UserRepository.class);

    ItemRequestService itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository,
            itemRepository);

    User testUser = User.of(1L, "Test user", "user@test.email");
    ItemRequest testItemRequest = ItemRequest.of(1L, "description", testUser, NOW_DATE_TIME);
    ItemRequestDto testItemRequestDto = ItemRequestDto.of(1L, "description",
            UserMapper.toUserDto(testUser), NOW_DATE_TIME, Collections.emptyList());

    @Test
    void createItemRequestByInvalidUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable e = assertThrows(NotFoundException.class, () -> itemRequestService.createItemRequest(
                testItemRequestDto, 1L), "Запрос от несуществующего пользователя не вызывает исключения");
        assertEquals("Запрос от несуществующего пользователя", e.getMessage(),
                "Неверное сообщение об ошибке");
    }

    @Test
    void createItemRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(testItemRequest);

        final ItemRequestDto itemRequestDto = itemRequestService.createItemRequest(testItemRequestDto, 1L);

        assertNotNull(itemRequestDto, "Возвращается пустой запрос");
        assertEquals(testItemRequestDto, itemRequestDto, "Возвращается неверный запрос");
    }

    @Test
    void getItemRequestsByRequestorIdByInvalidUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable e = assertThrows(NotFoundException.class, () -> itemRequestService
                .getItemRequestsByRequestorId(1L),
                "Запрос от несуществующего пользователя не вызывает исключения");
        assertEquals("Запрос от несуществующего пользователя", e.getMessage(),
                "Неверное сообщение об ошибке");
    }

    @Test
    void getItemRequestsByRequestorId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(itemRequestRepository.findAllByRequestorOrderByCreated(any(User.class)))
                .thenReturn(List.of(testItemRequest));

        final List<ItemRequestDto> itemRequestsDto = itemRequestService.getItemRequestsByRequestorId(1L);

        assertNotNull(itemRequestsDto, "Не возвращается список запросов");
        assertIterableEquals(List.of(testItemRequestDto), itemRequestsDto,
                "Возвращается неверный список запросов");
    }

    @Test
    void getItemRequestsByNotRequestorIdByInvalidUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable e = assertThrows(NotFoundException.class, () -> itemRequestService
                        .getItemRequestsByNotRequestorId(1L, 0, 10),
                "Запрос от несуществующего пользователя не вызывает исключения");
        assertEquals("Запрос от несуществующего пользователя", e.getMessage(),
                "Неверное сообщение об ошибке");
    }

    @Test
    void getItemRequestsByNotRequestorId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(itemRequestRepository.findAllByNotRequestorOrderByCreated(any(User.class), any(Pageable.class)))
                .thenReturn(Page.empty());

        final List<ItemRequestDto> itemRequestsDto = itemRequestService.getItemRequestsByNotRequestorId(1L,
                0, 10);

        assertNotNull(itemRequestsDto, "Не возвращается список запросов");
        assertTrue(itemRequestsDto.isEmpty(), "Возвращается неверный список запросов");
    }

    @Test
    void getRequestByIdByInvalidUser() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        Throwable e = assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(1L, 1L),
                "Запрос от несуществующего пользователя не вызывает исключения");
        assertEquals("Запрос от несуществующего пользователя", e.getMessage(),
                "Неверное сообщение об ошибке");
    }

    @Test
    void getRequestByIdByInvalidRequest() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable e = assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(1L, 1L),
                "Обращение к несуществующему запросу не вызывает исключения");
        assertEquals("Обращение к несуществующему запросу", e.getMessage(),
                "Неверное сообщение об ошибке");
    }

    @Test
    void getRequestById() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(testItemRequest));
        when(itemRepository.findAllByRequest(any(ItemRequest.class))).thenReturn(Collections.emptyList());

        final ItemRequestDto itemRequestDto = itemRequestService.getRequestById(1L, 1L);

        assertNotNull(itemRequestDto, "Возвращается пустой запрос");
        assertEquals(testItemRequestDto, itemRequestDto, "Возвращается неверный запрос");
    }
}