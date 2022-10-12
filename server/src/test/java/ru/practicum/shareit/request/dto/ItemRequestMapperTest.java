package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestMapper;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class ItemRequestMapperTest {

    static LocalDateTime NOW_DATE_TIME = LocalDateTime.now();

    User testUser = User.of(1L, "Test user", "test@user.email");
    ItemRequest testItemRequest = ItemRequest.of(1L, "description", testUser, NOW_DATE_TIME);
    ItemRequestDto testItemRequestDto = ItemRequestDto.of(1L, "description",
            UserMapper.toUserDto(testUser), NOW_DATE_TIME, Collections.emptyList());

    @Test
    void toItemRequestDto() {
        final ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(testItemRequest);
        assertNotNull(itemRequestDto, "Возвращается пустой DTO запроса");
        assertEquals(testItemRequestDto, itemRequestDto, "Возвращается неверный DTO запроса");
    }

    @Test
    void toItemRequestsDto() {
        final List<ItemRequestDto> itemRequestsDto = ItemRequestMapper.toItemRequestsDto(List.of(testItemRequest),
                (r) -> Collections.emptyList());
        assertNotNull(itemRequestsDto, "Не возвращается список DTO запросов");
        assertIterableEquals(List.of(testItemRequestDto), itemRequestsDto, "Возвращается неверный список");
    }

    @Test
    void fromItemRequestDto() {
        final ItemRequest itemRequest = ItemRequestMapper.fromItemRequestDto(testItemRequestDto);
        assertNotNull(itemRequest, "Не возвращается запрос");
        assertEquals(testItemRequest.getId(), itemRequest.getId(), "Возвращается неверный id запроса");
        assertEquals(testItemRequest.getDescription(), itemRequest.getDescription(),
                "Возвращается неверное описание запроса");
    }

    @Test
    void defaultConstructor() {
        final ItemRequestMapper itemRequestMapper = new ItemRequestMapper();
        assertNotNull(itemRequestMapper, "Объект маппера запросов не создается");
        assertInstanceOf(ItemRequestMapper.class, itemRequestMapper, "Создается объект неверного класса");
    }
}