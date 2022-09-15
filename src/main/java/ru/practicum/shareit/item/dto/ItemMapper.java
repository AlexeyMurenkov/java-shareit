package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.CommentMapper.toCommentsDto;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.of(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static List<ItemDto> toItemsDto(List<Item> items) {
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public static Item fromItemDto(ItemDto itemDto, Long userId) {
        return Item.of(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                userId,
                null
        );
    }

    public static ItemGetDto toItemGetDto(Item item, Optional<Booking> lastBooking, Optional<Booking> nextBooking,
                                          List<Comment> comments) {
        return ItemGetDto.of(
            item.getId(),
            item.getName(),
            item.getDescription(),
            item.isAvailable(),
                lastBooking.map(booking -> ItemGetDto.BookingDto.of(
                        booking.getId(),
                        booking.getBooker().getId(),
                        booking.getStart(),
                        booking.getEnd()
                )).orElse(null),
                nextBooking.map(booking -> ItemGetDto.BookingDto.of(
                        booking.getId(),
                        booking.getBooker().getId(),
                        booking.getStart(),
                        booking.getEnd()
                )).orElse(null),
                toCommentsDto(comments)
        );
    }
}
