package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.dto.BookingDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestBody BookingDto bookingDto) {
        log.debug("Добавление аренды пользователем id={}", userId);
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveReject(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId,
                                    @RequestParam Boolean approved) {
        log.debug("Добавление статуса аренды {} пользователем id={}", bookingId, userId);
        return bookingService.approveRejectBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        log.debug("Получение сведений об аренде {} пользователем id={}", bookingId, userId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getByBookerIdAndState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam String state,
                                                  @RequestParam int from,
                                                  @RequestParam int size) {
        return bookingService.getAllBookingsByBookerAndState(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getByOwnerIdAndState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam String state,
                                                 @RequestParam int from,
                                                 @RequestParam int size) {
        return bookingService.getAllBookingsByOwnerAndState(userId, state, from, size);
    }
}
