package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.BookingDto;
import ru.practicum.shareit.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Map;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
							 @RequestBody @Valid BookingDto bookingDto) {
		if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
			throw new IllegalArgumentException("Начало аренды не может быть позже окончания");
		}
		log.debug("Добавление аренды пользователем id={}", userId);
		return bookingClient.create(userId, bookingDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approveReject(@RequestHeader("X-Sharer-User-Id") Long userId,
									@PathVariable Long bookingId, @RequestParam @NotNull Boolean approved) {
		log.debug("Добавление статуса аренды {} пользователем id={}", bookingId, userId);
		return bookingClient.approveReject(userId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
							  @PathVariable Long bookingId) {
		log.debug("Получение сведений об аренде {} пользователем id={}", bookingId, userId);
		return bookingClient.getById(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getByBookerIdAndState(@RequestHeader("X-Sharer-User-Id") Long userId,
														@RequestParam(defaultValue = "ALL") String state,
														@RequestParam(defaultValue = "0") @PositiveOrZero int from,
														@RequestParam(defaultValue = "10") @Positive int size) {
		final BookingState stateFromString = BookingState.from(state)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));

		log.debug("Получение сведений об аренде со статусом {} арендатором {}", state, userId);
		return bookingClient.getByBookerIdAndState(userId, stateFromString, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getByOwnerIdAndState(@RequestHeader("X-Sharer-User-Id") Long userId,
													   @RequestParam(defaultValue = "ALL") String state,
													   @RequestParam(defaultValue = "0") @PositiveOrZero int from,
													   @RequestParam(defaultValue = "10") @Positive int size) {
		final BookingState stateFromString = BookingState.from(state)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));

		log.debug("Получение сведений об аренде со статусом {} владельцем {}", state, userId);
		return bookingClient.getByOwnerIdAndState(userId, stateFromString, from, size);
	}

	@ExceptionHandler
	public ResponseEntity<Object> handleIllegalArgumentException(final IllegalArgumentException e) {
		return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
	}
}
