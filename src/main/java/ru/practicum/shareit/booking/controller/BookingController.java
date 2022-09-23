package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                             @RequestBody @NotNull @Valid BookingDto bookingDto) {
        log.debug("Добавление аренды пользователем id={}", userId);
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveReject(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                    @PathVariable @NotNull Long bookingId, @RequestParam @NotNull Boolean approved) {
        log.debug("Добавление статуса аренды {} пользователем id={}", bookingId, userId);
        return bookingService.approveRejectBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                              @PathVariable @NotNull Long bookingId) {
        log.debug("Получение сведений об аренде {} пользователем id={}", bookingId, userId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getByBookerIdAndState(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                                  @RequestParam(defaultValue = "ALL") String state,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                  @RequestParam(defaultValue = "10") @Positive int size) {
        return bookingService.getAllBookingsByBookerAndState(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getByOwnerIdAndState(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                                 @RequestParam(defaultValue = "ALL") String state,
                                                 @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                 @RequestParam(defaultValue = "10") @Positive int size) {
        return bookingService.getAllBookingsByOwnerAndState(userId, state, from, size);
    }
}
