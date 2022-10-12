package ru.practicum.shareit.dto;

import java.util.Arrays;
import java.util.Optional;

public enum BookingState {
	ALL, CURRENT, FUTURE, PAST,	REJECTED, WAITING;

	public static Optional<BookingState> from(String stringState) {
		return Arrays.stream(values())
				.filter((value) -> value.name().equalsIgnoreCase(stringState))
				.findAny();
	}
}
