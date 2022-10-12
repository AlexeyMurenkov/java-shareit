package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.exception.BadRequestException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
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

    static Sort SORT_BY_START_DESC = Sort.by("start").descending();
    BookingRepository bookingRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(
                        String.format("Обращение к бронированиям от несуществующего пользователя (%s)", userId)
                )
        );
    }

    private Booking getBookingDtoById(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException(String.format("Обращение к несуществующему бронированию (%s)", bookingId))
        );
    }

    @Override
    public BookingDto createBooking(BookingDto bookingDto, Long userId) {
        final User user = getUser(userId);
        final Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(
                () -> new NotFoundException(String.format("Попытка аренды несуществующей вещи (id=%s)",
                        bookingDto.getItemId()))
        );

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
        getUser(userId);
        final Booking booking = getBookingDtoById(bookingId);
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
        getUser(userId);
        final Booking booking = getBookingDtoById(bookingId);
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwnerId().equals(userId)) {
            throw new NotFoundException(
                    "Попытка получения сведений о бронировании не владельцем вещи или не создалелем запроса на аренду"
            );
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookingsByBookerAndState(Long userId, String state, int from, int size) {
        final User user = getUser(userId);
        final Pageable pageable = PageRequest.of(from / size, size, SORT_BY_START_DESC);

        switch (BookingState.valueOf(state)) {
            case ALL: return BookingMapper.toBookingsDto(bookingRepository.findAllByBooker(user, pageable));
            case CURRENT: return BookingMapper.toBookingsDto(bookingRepository.findAllByBookerStateCurrent(user,
                    pageable));
            case FUTURE: return BookingMapper.toBookingsDto(bookingRepository.findAllByBookerAndStartAfter(user,
                    LocalDateTime.now(), pageable));
            case PAST: return BookingMapper.toBookingsDto(bookingRepository.findAllByBookerAndEndBefore(user,
                    LocalDateTime.now(), pageable));
            case WAITING: return BookingMapper.toBookingsDto(bookingRepository.findAllByBookerAndStatus(user,
                    BookingStatus.WAITING, pageable));
            case REJECTED: return BookingMapper.toBookingsDto(bookingRepository.findAllByBookerAndStatus(user,
                    BookingStatus.REJECTED, pageable));
            default: return Collections.emptyList();
        }
    }

    @Override
    public List<BookingDto> getAllBookingsByOwnerAndState(Long userId, String state, int from, int size) {
        getUser(userId);
        final Pageable pageable = PageRequest.of(from / size, size, SORT_BY_START_DESC);

        switch (BookingState.valueOf(state)) {
            case ALL: return BookingMapper.toBookingsDto(bookingRepository.findAllByItemOwnerId(userId, pageable));
            case CURRENT: return BookingMapper.toBookingsDto(bookingRepository.findAllByItemOwnerIdStateCurrent(userId,
                    pageable));
            case FUTURE: return BookingMapper.toBookingsDto(bookingRepository.findAllByItemOwnerIdAndStartAfter(userId,
                    LocalDateTime.now(), pageable));
            case PAST: return BookingMapper.toBookingsDto(bookingRepository.findAllByItemOwnerIdAndEndBefore(userId,
                    LocalDateTime.now(), pageable));
            case WAITING: return BookingMapper.toBookingsDto(bookingRepository.findAllByItemOwnerIdAndStatus(userId,
                    BookingStatus.WAITING, pageable));
            case REJECTED: return BookingMapper.toBookingsDto(bookingRepository.findAllByItemOwnerIdAndStatus(userId,
                    BookingStatus.REJECTED, pageable));
            default: return Collections.emptyList();
        }
    }
}
