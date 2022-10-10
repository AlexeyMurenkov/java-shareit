package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.exceptoins.BadRequestException;
import ru.practicum.shareit.common.exceptoins.NotFoundException;
import ru.practicum.shareit.common.exceptoins.ValidationException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class BookingServiceTest {

    static LocalDateTime NOW_DATE_TIME = LocalDateTime.now();

    BookingRepository bookingRepository = mock(BookingRepository.class);
    UserRepository userRepository = mock(UserRepository.class);
    ItemRepository itemRepository = mock(ItemRepository.class);

    BookingService bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);

    User testUser = User.of(1L, "Test user", "user@test.email");
    ItemRequest testItemRequest = ItemRequest.of(1L, "description", testUser, NOW_DATE_TIME);
    Item testItem = Item.of(1L, "Test item", "description", true, 1L,
            testItemRequest);
    Item testItemOtherOwner = Item.of(1L, "Test item", "description", false, 2L,
            testItemRequest);

    BookingDto testBookingDto = BookingDto.of(1L, NOW_DATE_TIME, NOW_DATE_TIME, BookingStatus.APPROVED,
            UserMapper.toUserDto(testUser), ItemMapper.toItemDto(testItem), 1L);
    Booking testBooking = Booking.of(1L, NOW_DATE_TIME, NOW_DATE_TIME, testItem, testUser, BookingStatus.APPROVED);

    @Test
    void createBookingByInvalidUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable e = assertThrows(NotFoundException.class, () -> bookingService.createBooking(testBookingDto,
                1L), "Запрос от несуществующего пользователя не вызывает исключения");
        assertEquals("Обращение к бронированиям от несуществующего пользователя (1)", e.getMessage(),
                "Неверное сообщение об ошибке");
    }

    @Test
    void createBookingWithInvalidItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable e = assertThrows(NotFoundException.class, () -> bookingService.createBooking(testBookingDto,
                1L), "Запрос от несуществующей вещи не вызывает исключения");
        assertEquals("Попытка аренды несуществующей вещи (id=1)", e.getMessage(),
                "Неверное сообщение об ошибке");
    }

    @Test
    void createBookingOwnItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItem));

        Throwable e = assertThrows(NotFoundException.class, () -> bookingService.createBooking(testBookingDto,
                1L), "Запрос от несуществующей вещи не вызывает исключения");
        assertEquals("Попытка резервирования собственной вещи", e.getMessage(),
                "Неверное сообщение об ошибке");
    }

    @Test
    void createBookingUnavailableItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItemOtherOwner));

        Throwable e = assertThrows(BadRequestException.class, () -> bookingService.createBooking(testBookingDto,
                1L), "Запрос от несуществующей вещи не вызывает исключения");
        assertEquals("Попытка резервирования недоступной вещи", e.getMessage(),
                "Неверное сообщение об ошибке");
    }

    @Test
    void createBookingWithInvalidTime() {
        final BookingDto testBookingDto = BookingDto.of(1L, NOW_DATE_TIME, NOW_DATE_TIME.minusSeconds(1),
                BookingStatus.APPROVED, UserMapper.toUserDto(testUser), ItemMapper.toItemDto(testItem), 1L);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItemOtherOwner));

        Throwable e = assertThrows(ValidationException.class, () -> bookingService.createBooking(testBookingDto,
                1L), "Запрос c неверным временем не вызывает исключения");
        assertEquals("Ошибка валидации", e.getMessage(),
                "Неверное сообщение об ошибке");
    }

    @Test
    void createBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItem));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        final BookingDto bookingDto = bookingService.createBooking(testBookingDto, 2L);

        assertNotNull(bookingDto, "Не создается аренда");
        assertEquals(testBookingDto.getId(), bookingDto.getId(), "Возвращается неверный id аренды");
    }

    @Test
    void approveRejectBookingWithInvalidItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable e = assertThrows(NotFoundException.class, () -> bookingService.approveRejectBooking(1L,
                1L, true), "Запрос несуществующей вещи не вызывает исключения");
        assertEquals("Обращение к несуществующему бронированию (1)", e.getMessage(),
                "Неверное сообщение об ошибке");
    }

    @Test
    void approveRejectBookingByInvalidUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable e = assertThrows(NotFoundException.class, () -> bookingService.approveRejectBooking(1L,
                1L, true), "Запрос от несуществующего пользователя не вызывает исключения");
        assertEquals("Обращение к бронированиям от несуществующего пользователя (1)", e.getMessage(),
                "Неверное сообщение об ошибке");
    }

    @Test
    void approveRejectBookingByNotOwner() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItemOtherOwner));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(testBooking));

        Throwable e = assertThrows(NotFoundException.class, () -> bookingService.approveRejectBooking(1L,
                        2L, true),
                "Запрос от не владельца не вызывает исключения");
        assertEquals(
                "Попытка изменения статуса бронирования вещи, принадлежащей другому пользователю",
                e.getMessage(),
                "Неверное сообщение об ошибке"
        );
    }

    @Test
    void approveRejectBookingWithApprovedStatus() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItemOtherOwner));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(testBooking));

        Throwable e = assertThrows(BadRequestException.class, () -> bookingService.approveRejectBooking(1L,
                        1L, true),
                "Запрос измения статуса подтвержденной аренды не вызывает исключения");
        assertEquals(
                "Нельзя изменить статус подтвержденного бронирования",
                e.getMessage(),
                "Неверное сообщение об ошибке"
        );
    }

    @Test
    void approveRejectBookingSetApprove() {
        final Booking testBooking = Booking.of(1L, NOW_DATE_TIME, NOW_DATE_TIME, testItem, testUser,
                BookingStatus.WAITING);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItemOtherOwner));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        final BookingDto bookingDto = bookingService.approveRejectBooking(1L, 1L, true);
        assertNotNull(bookingDto, "Не возвращается аренда");
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus(), "Не изменяется статус аренды");
    }

    @Test
    void approveRejectBookingSetReject() {
        final Booking testBooking = Booking.of(1L, NOW_DATE_TIME, NOW_DATE_TIME, testItem, testUser,
                BookingStatus.WAITING);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItemOtherOwner));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        final BookingDto bookingDto = bookingService.approveRejectBooking(1L, 1L, false);
        assertNotNull(bookingDto, "Не возвращается аренда");
        assertEquals(BookingStatus.REJECTED, bookingDto.getStatus(), "Не изменяется статус аренды");
    }

    @Test
    void getBookingByIdByInvalidUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable e = assertThrows(NotFoundException.class, () -> bookingService.getBookingById(1L, 1L),
                "Запрос от несуществующего пользователя не вызывает исключения");
        assertEquals("Обращение к бронированиям от несуществующего пользователя (1)", e.getMessage(),
                "Неверное сообщение об ошибке");
    }

    @Test
    void getBookingWithInvalidBookingId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItemOtherOwner));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable e = assertThrows(NotFoundException.class, () -> bookingService.getBookingById(1L, 1L),
                "Запрос от несуществующей аренды не вызывает исключения");
        assertEquals(
                "Обращение к несуществующему бронированию (1)",
                e.getMessage(),
                "Неверное сообщение об ошибке"
        );
    }

    @Test
    void getBookingByIdByNotOwner() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItemOtherOwner));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(testBooking));

        Throwable e = assertThrows(NotFoundException.class, () -> bookingService.getBookingById(1L, 2L),
                "Запрос от не владельца не вызывает исключения");
        assertEquals(
                "Попытка получения сведений о бронировании не владельцем вещи или не создалелем запроса на аренду",
                e.getMessage(),
                "Неверное сообщение об ошибке"
        );
    }

    @Test
    void getBooking() {
        final BookingDto testBookingDto = BookingDto.of(1L, NOW_DATE_TIME, NOW_DATE_TIME, BookingStatus.APPROVED,
                UserMapper.toUserDto(testUser), ItemMapper.toItemDto(testItem), null);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItemOtherOwner));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(testBooking));

        final BookingDto bookingDto = bookingService.getBookingById(1L, 1L);

        assertNotNull(bookingDto, "Не возвращается аренда");
        assertEquals(testBookingDto, bookingDto, "Возвращается неверная аренда");
    }

    @Test
    void getAllBookingsByBookerAndStateByInvalidUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable e = assertThrows(NotFoundException.class, () -> bookingService.getAllBookingsByBookerAndState(
                1L, "", 0, 10),
                "Запрос от несуществующего пользователя не вызывает исключения");
        assertEquals("Обращение к бронированиям от несуществующего пользователя (1)", e.getMessage(),
                "Неверное сообщение об ошибке");
    }

    @Test
    void getAllBookingsByBookerAndStateAll() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(bookingRepository.findAllByBooker(any(User.class), any(Pageable.class))).thenReturn(List.of(testBooking));

        final List<BookingDto> bookingsDto = bookingService.getAllBookingsByBookerAndState(1L, "ALL",
                0, 10);

        assertNotNull(bookingsDto, "Не возвращается список аренд");
        assertFalse(bookingsDto.isEmpty(), "Возвращается пустой список аренд");
    }

    @Test
    void getAllBookingsByBookerAndStateCurrent() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(bookingRepository.findAllByBookerStateCurrent(any(User.class), any(Pageable.class)))
                .thenReturn(List.of(testBooking));

        final List<BookingDto> bookingsDto = bookingService.getAllBookingsByBookerAndState(1L, "CURRENT",
                0, 10);

        assertNotNull(bookingsDto, "Не возвращается список аренд");
        assertFalse(bookingsDto.isEmpty(), "Возвращается пустой список аренд");
    }

    @Test
    void getAllBookingsByBookerAndStateFuture() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(bookingRepository.findAllByBookerAndStartAfter(any(User.class), any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(List.of(testBooking));

        final List<BookingDto> bookingsDto = bookingService.getAllBookingsByBookerAndState(1L, "FUTURE",
                0, 10);

        assertNotNull(bookingsDto, "Не возвращается список аренд");
        assertFalse(bookingsDto.isEmpty(), "Возвращается пустой список аренд");
    }

    @Test
    void getAllBookingsByBookerAndStatePast() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(bookingRepository.findAllByBookerAndEndBefore(any(User.class), any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(List.of(testBooking));

        final List<BookingDto> bookingsDto = bookingService.getAllBookingsByBookerAndState(1L, "PAST",
                0, 10);

        assertNotNull(bookingsDto, "Не возвращается список аренд");
        assertFalse(bookingsDto.isEmpty(), "Возвращается пустой список аренд");
    }

    @Test
    void getAllBookingsByBookerAndStateWaiting() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(bookingRepository.findAllByBookerAndStatus(any(User.class), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(List.of(testBooking));

        final List<BookingDto> bookingsDto = bookingService.getAllBookingsByBookerAndState(1L, "WAITING",
                0, 10);

        assertNotNull(bookingsDto, "Не возвращается список аренд");
        assertFalse(bookingsDto.isEmpty(), "Возвращается пустой список аренд");
    }

    @Test
    void getAllBookingsByBookerAndStateRejected() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(bookingRepository.findAllByBookerAndStatus(any(User.class), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(List.of(testBooking));

        final List<BookingDto> bookingsDto = bookingService.getAllBookingsByBookerAndState(1L, "REJECTED",
                0, 10);

        assertNotNull(bookingsDto, "Не возвращается список аренд");
        assertFalse(bookingsDto.isEmpty(), "Возвращается пустой список аренд");
    }

    @Test
    void getAllBookingsByBookerAndStateUnknown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        Throwable e = assertThrows(BadRequestException.class, () -> bookingService.getAllBookingsByBookerAndState(
                1L, "unknown",0, 10),
                "Запрос с неподдерживаемым статусом не вызывает исключения");
        assertEquals("Unknown state: UNSUPPORTED_STATUS", e.getMessage(),"Неверное сообщение об ошибке");
    }

    @Test
    void getAllBookingsByOwnerAndStateByInvalidUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable e = assertThrows(NotFoundException.class, () -> bookingService.getAllBookingsByOwnerAndState(
                        1L, "", 0, 10),
                "Запрос от несуществующего пользователя не вызывает исключения");
        assertEquals("Обращение к бронированиям от несуществующего пользователя (1)", e.getMessage(),
                "Неверное сообщение об ошибке");
    }

    @Test
    void getAllBookingsByOwnerAndStateAll() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(bookingRepository.findAllByItemOwnerId(anyLong(), any(Pageable.class))).thenReturn(List.of(testBooking));

        final List<BookingDto> bookingsDto = bookingService.getAllBookingsByOwnerAndState(1L, "ALL",
                0, 10);

        assertNotNull(bookingsDto, "Не возвращается список аренд");
        assertFalse(bookingsDto.isEmpty(), "Возвращается пустой список аренд");
    }

    @Test
    void getAllBookingsByOwnerAndStateCurrent() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(bookingRepository.findAllByItemOwnerIdStateCurrent(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(testBooking));

        final List<BookingDto> bookingsDto = bookingService.getAllBookingsByOwnerAndState(1L, "CURRENT",
                0, 10);

        assertNotNull(bookingsDto, "Не возвращается список аренд");
        assertFalse(bookingsDto.isEmpty(), "Возвращается пустой список аренд");
    }

    @Test
    void getAllBookingsByOwnerAndStateFuture() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(bookingRepository.findAllByItemOwnerIdAndStartAfter(anyLong(), any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(List.of(testBooking));

        final List<BookingDto> bookingsDto = bookingService.getAllBookingsByOwnerAndState(1L, "FUTURE",
                0, 10);

        assertNotNull(bookingsDto, "Не возвращается список аренд");
        assertFalse(bookingsDto.isEmpty(), "Возвращается пустой список аренд");
    }

    @Test
    void getAllBookingsByOwnerAndStatePast() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(bookingRepository.findAllByItemOwnerIdAndEndBefore(anyLong(), any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(List.of(testBooking));

        final List<BookingDto> bookingsDto = bookingService.getAllBookingsByOwnerAndState(1L, "PAST",
                0, 10);

        assertNotNull(bookingsDto, "Не возвращается список аренд");
        assertFalse(bookingsDto.isEmpty(), "Возвращается пустой список аренд");
    }

    @Test
    void getAllBookingsByOwnerAndStateWaiting() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(bookingRepository.findAllByItemOwnerIdAndStatus(anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(List.of(testBooking));

        final List<BookingDto> bookingsDto = bookingService.getAllBookingsByOwnerAndState(1L, "WAITING",
                0, 10);

        assertNotNull(bookingsDto, "Не возвращается список аренд");
        assertFalse(bookingsDto.isEmpty(), "Возвращается пустой список аренд");
    }

    @Test
    void getAllBookingsByOwnerAndStateRejected() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(bookingRepository.findAllByItemOwnerIdAndStatus(anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(List.of(testBooking));

        final List<BookingDto> bookingsDto = bookingService.getAllBookingsByOwnerAndState(1L, "REJECTED",
                0, 10);

        assertNotNull(bookingsDto, "Не возвращается список аренд");
        assertFalse(bookingsDto.isEmpty(), "Возвращается пустой список аренд");
    }

    @Test
    void getAllBookingsByOwnerAndStateUnknown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        Throwable e = assertThrows(BadRequestException.class, () -> bookingService.getAllBookingsByOwnerAndState(
                        1L, "unknown",0, 10),
                "Запрос с неподдерживаемым статусом не вызывает исключения");
        assertEquals("Unknown state: UNSUPPORTED_STATUS", e.getMessage(),"Неверное сообщение об ошибке");
    }
}