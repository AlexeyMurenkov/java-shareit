package ru.practicum.shareit.item.dto;

import lombok.Value;
import ru.practicum.shareit.dto.CommentDto;

import java.time.LocalDateTime;
import java.util.List;

@Value(staticConstructor = "of")
public class ItemGetDto {
    Long id;
    String name;
    String description;
    Boolean available;
    BookingDto lastBooking;
    BookingDto nextBooking;
    List<CommentDto> comments;

    @Value(staticConstructor = "of")
    public static class BookingDto {
        Long id;
        Long bookerId;
        LocalDateTime start;
        LocalDateTime end;
    }
}
