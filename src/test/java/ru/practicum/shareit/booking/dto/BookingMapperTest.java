package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class BookingMapperTest {

    static LocalDateTime NOW_DATE_TIME = LocalDateTime.now();

    User testUser = User.of(1L, "Test user", "test@user.email");
    Item testItem = Item.of(1L, "Item 1", "Item 1 description", true, 2L,
            null);
    Booking testBooking = Booking.of(1L, NOW_DATE_TIME, NOW_DATE_TIME, testItem, testUser, BookingStatus.APPROVED);
    BookingDto testBookingDto = BookingDto.of(1L, NOW_DATE_TIME, NOW_DATE_TIME, BookingStatus.APPROVED,
            UserMapper.toUserDto(testUser), ItemMapper.toItemDto(testItem), null);

    @Test
    void toBookingDto() {
        final BookingDto bookingDto = BookingMapper.toBookingDto(testBooking);
        assertNotNull(bookingDto, "Возвращается пустой DTO аренды");
        assertEquals(testBookingDto, bookingDto, "Возвращается неверный DTO аренды");
    }

    @Test
    void toBookingDtoShouldReturnNull() {
        assertNull(BookingMapper.toBookingDto(null), "Для пустой аренды возвращается DTO");
    }

    @Test
    void toBookingsDto() {
        final List<BookingDto> bookingsDto = BookingMapper.toBookingsDto(List.of(testBooking));
        assertNotNull(bookingsDto, "Не возвращается список аренд");
        assertIterableEquals(List.of(testBookingDto), bookingsDto, "Возвращается неверный список DTO аренд");
    }

    @Test
    void fromBookingDto() {
        final Booking booking = BookingMapper.fromBookingDto(testBookingDto, UserMapper.toUserDto(testUser),
                ItemMapper.toItemDto(testItem));
        assertNotNull(booking, "Возвращается пустая аренда");
        assertEquals(testBooking.getId(), booking.getId(), "Возвращается неверная арендв");
    }

    @Test
    void defaultConstructor() {
        final BookingMapper bookingMapper = new BookingMapper();
        assertNotNull(bookingMapper, "Объект маппера аренд не создается");
        assertInstanceOf(BookingMapper.class, bookingMapper, "Создается объект неверного класса");
    }
}