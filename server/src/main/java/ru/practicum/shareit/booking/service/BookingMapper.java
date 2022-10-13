package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.dto.BookingDto;
import ru.practicum.shareit.dto.BookingStatus;
import ru.practicum.shareit.dto.ItemDto;
import ru.practicum.shareit.dto.UserDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        if (booking == null) return null;
        return BookingDto.of(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                UserMapper.toUserDto(booking.getBooker()),
                ItemMapper.toItemDto(booking.getItem()),
                null
        );
    }

    public static List<BookingDto> toBookingsDto(List<Booking> bookings) {
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    public static Booking fromBookingDto(BookingDto bookingDto, UserDto userDto, ItemDto itemDto) {
        return Booking.of(
                bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                ItemMapper.fromItemDto(itemDto, userDto.getId()),
                UserMapper.fromUserDto(userDto),
                Optional.ofNullable(bookingDto.getStatus()).orElse(BookingStatus.WAITING)
        );
    }
}
