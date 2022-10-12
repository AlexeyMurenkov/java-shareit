package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class ItemMapperTest {

    static LocalDateTime NOW_DATE_TIME = LocalDateTime.now();

    Item testItem = Item.of(1L, "Item 1", "Item 1 description", true, 2L,
            null);

    ItemDto testItemDto = ItemDto.of(1L, "Item 1", "Item 1 description", true, null);

    User testUser = User.of(1L, "Test user", "test@user.email");

    Booking testBooking = Booking.of(1L, NOW_DATE_TIME, NOW_DATE_TIME, null, testUser, null);

    ItemGetDto.BookingDto testBookingDto = ItemGetDto.BookingDto.of(1L, 1L, NOW_DATE_TIME,
            NOW_DATE_TIME);

    @Test
    void toItemDto() {
        final ItemDto itemDto = ItemMapper.toItemDto(testItem);
        assertNotNull(itemDto, "Возвращается пустое DTO вещи");
        assertEquals(testItemDto, itemDto, "Возвращается неверное DTO вещи");
    }

    @Test
    void toItemsDto() {
        final List<ItemDto> itemsDto = ItemMapper.toItemsDto(List.of(testItem));
        assertNotNull(itemsDto, "Не возвращается список DTO вещей");
        assertIterableEquals(List.of(testItemDto), itemsDto, "Возвращается неверный список DTO вещей");
    }

    @Test
    void fromItemDto() {
        final Item item = ItemMapper.fromItemDto(testItemDto, 1L);
        assertNotNull(item, "Не возвращается вещь");
        assertEquals(testItemDto.getId(), item.getId(), "Возвращается неверный id вещи");
        assertEquals(testItemDto.getName(), item.getName(), "Возвращается неверный name вещи");
        assertEquals(testItemDto.getDescription(), item.getDescription(),
                "Возвращается неверное описание вещи");
    }

    @Test
    void toItemGetDto() {
        final ItemGetDto testGetDto = ItemGetDto.of(1L, "Item 1", "Item 1 description", true,
                testBookingDto, testBookingDto, Collections.emptyList());

        final ItemGetDto itemGetDto = ItemMapper.toItemGetDto(testItem, Optional.of(testBooking),
                Optional.of(testBooking), Collections.emptyList());
        assertNotNull(itemGetDto, "Не возвращается getDto вещи");
        assertEquals(testGetDto, itemGetDto, "Возвращается неверное getDTO вещи");
    }

    @Test
    void defaultConstructor() {
        final ItemMapper itemMapper = new ItemMapper();
        assertNotNull(itemMapper, "Объект маппера вещей не создается");
        assertInstanceOf(ItemMapper.class, itemMapper, "Создается объект неверного класса");
    }
}