package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(BookingDto bookingDto, Long userId);

    BookingDto approveRejectBooking(Long bookingId, Long userId, boolean approved);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getAllBookingsByBookerAndState(Long userId, String state, int from, int size);

    List<BookingDto> getAllBookingsByOwnerAndState(Long userId, String state, int from, int size);
}
