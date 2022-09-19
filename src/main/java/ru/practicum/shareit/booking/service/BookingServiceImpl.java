package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.exceptoins.BadRequestException;
import ru.practicum.shareit.common.exceptoins.NotFoundException;
import ru.practicum.shareit.common.exceptoins.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static ru.practicum.shareit.item.dto.ItemMapper.toItemDto;
import static ru.practicum.shareit.user.dto.UserMapper.toUserDto;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BookingServiceImpl implements BookingService {
    BookingRepository bookingRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;

    private void checkUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Обращение к бронированиям от несуществующего пользователя (%s)",
                    userId));
        }
    }

    private BookingState convertState(String state) {
        try {
            return BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private Booking getBookingDtoByIdAndUserId(Long bookingId, Long userId) {
        checkUser(userId);
        return bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException(String.format("Обращение к несуществующему бронированию (%s)", bookingId))
        );
    }

    @Override
    public BookingDto createBooking(BookingDto bookingDto, Long userId) {
        final User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Создание аренды несуществующим пользователя (%s)", userId))
        );
        if (!itemRepository.existsById(bookingDto.getItemId())) {
            throw new NotFoundException(String.format("Попытка аренды несуществующей вещи (id=%s)",
                    bookingDto.getItemId()));
        }
        final Item item = itemRepository.getReferenceById(bookingDto.getItemId());
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ValidationException("Начало аренды не может быть позже окончания");
        }
        final UserDto userDto = toUserDto(user);
        final ItemDto itemDto = toItemDto(item);
        final Booking booking = BookingMapper.fromBookingDto(bookingDto, userDto, itemDto);
        if (item.getOwnerId().equals(userId)) {
            throw new NotFoundException("Попытка резервирования собственной вещи");
        }
        if (!booking.getItem().isAvailable()) {
            throw new BadRequestException("Попытка резервирования недоступной вещи");
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approveRejectBooking(Long bookingId, Long userId, boolean approved) {
        checkUser(userId);
        final Booking booking = getBookingDtoByIdAndUserId(bookingId, userId);
        if (!booking.getItem().getOwnerId().equals(userId)) {
            throw new NotFoundException(
                    "Попытка изменения статуса бронирования вещи, принадлежащей другому пользователю"
            );
        }
        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new BadRequestException("Нельзя изменить статус подтвержденного бронирования");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        checkUser(userId);
        final Booking booking = getBookingDtoByIdAndUserId(bookingId, userId);
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwnerId().equals(userId)) {
            throw new NotFoundException(
                    "Попытка получения сведений о бронировании не владельцем вещи или не создалелем запроса на аренду"
            );
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookingsByBookerAndState(Long userId, String state) {
        checkUser(userId);
        switch (convertState(state)) {
            case ALL: return BookingMapper.toBookingsDto(bookingRepository.findAllByBookerIdOrderByStartDesc(userId));
            case CURRENT: return BookingMapper.toBookingsDto(bookingRepository.findAllByBookerIdStateCurrent(userId));
            case FUTURE: return BookingMapper.toBookingsDto(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId,
                    LocalDateTime.now()));
            case PAST: return BookingMapper.toBookingsDto(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId,
                    LocalDateTime.now()));
            case WAITING: return BookingMapper.toBookingsDto(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId,
                    BookingStatus.WAITING));
            case REJECTED: return BookingMapper.toBookingsDto(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId,
                    BookingStatus.REJECTED));
            default: return Collections.emptyList();
        }
    }

    @Override
    public List<BookingDto> getAllBookingsByOwnerAndState(Long userId, String state) {
        checkUser(userId);
        switch (convertState(state)) {
            case ALL: return BookingMapper.toBookingsDto(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId));
            case CURRENT: return BookingMapper.toBookingsDto(bookingRepository.findAllByItemOwnerIdStateCurrent(userId));
            case FUTURE: return BookingMapper.toBookingsDto(bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId,
                    LocalDateTime.now()));
            case PAST: return BookingMapper.toBookingsDto(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId,
                    LocalDateTime.now()));
            case WAITING: return BookingMapper.toBookingsDto(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId,
                    BookingStatus.WAITING));
            case REJECTED: return BookingMapper.toBookingsDto(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId,
                    BookingStatus.REJECTED));
            default: return Collections.emptyList();
        }
    }
}
