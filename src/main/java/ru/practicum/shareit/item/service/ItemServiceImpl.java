package ru.practicum.shareit.item.service;

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
import ru.practicum.shareit.common.ModelValidator;
import ru.practicum.shareit.common.exceptoins.BadRequestException;
import ru.practicum.shareit.common.exceptoins.ForbiddenException;
import ru.practicum.shareit.common.exceptoins.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemGetDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.CommentMapper.fromCommentDto;
import static ru.practicum.shareit.item.dto.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.dto.ItemMapper.*;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {

    static Sort SORT_BY_START_ASC = Sort.by("id").ascending();

    ItemRepository itemRepository;
    ItemRequestRepository itemRequestRepository;
    UserRepository userRepository;
    BookingRepository bookingRepository;
    CommentRepository commentRepository;
    ModelValidator<ItemDto> modelValidator;

    private ItemDto patchItemDto(ItemDto recipient, ItemDto donor) {
        return ItemDto.of(
                recipient.getId(),
                Optional.ofNullable(donor.getName()).orElse(recipient.getName()),
                Optional.ofNullable(donor.getDescription()).orElse(recipient.getDescription()),
                Optional.ofNullable(donor.getAvailable()).orElse(recipient.getAvailable()),
                recipient.getRequestId()
        );
    }

    private ItemGetDto toItemGetDto(Item item, User user) {
        final Optional<Booking> lastBooking;
        final Optional<Booking> nextBooking;
        if (item.getOwnerId().equals(user.getId())) {
            lastBooking = bookingRepository.findFirstByItemAndEndBeforeOrderByEndDesc(item, LocalDateTime.now());
            nextBooking = bookingRepository.findFirstByItemAndStartAfterOrderByStartDesc(item, LocalDateTime.now());
        } else {
            lastBooking = Optional.empty();
            nextBooking = Optional.empty();
        }

        final List<Comment> comments = commentRepository.findAllByItemOrderByCreated(item);

        return ItemMapper.toItemGetDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    public List<ItemGetDto> getItemsByUserId(Long userId, int from, int size) {
        final User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Запрос вещи несуществующим пользователем (id=%s)", userId))
        );

        final Pageable pageable = PageRequest.of(from / size, size, SORT_BY_START_ASC);
        final List<Item> items = itemRepository.findAllByOwnerId(userId, pageable);

        return items.stream().map(
                (item) -> toItemGetDto(item, user)
        ).collect(Collectors.toList());
    }

    @Override
    public ItemGetDto getItemById(Long itemId, Long userId) {

        final User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Запрос вещи несуществующим пользователем (id=%s)", userId))
        );

        final Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException(String.format("Вещь с id=%s не найдена", itemId))
        );

        return toItemGetDto(item, user);
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Попытка создания вещи несуществующим пользователем (id=%s)",
                    userId));
        }
        modelValidator.apply(itemDto);
        final Long requestId = itemDto.getRequestId();
        final ItemRequest itemRequest = requestId != null ? itemRequestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Создание вещи для несуществующего запроса")
        ) : null;
        final Item item = fromItemDto(itemDto, userId, itemRequest);
        final Item createdItem = itemRepository.save(item);

        return toItemDto(createdItem);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Попытка создания вещи несуществующим пользователем (id=%s)",
                    userId));
        }
        final Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException(String.format("Попытка обновления несуществующей вещи (id=%s)", itemId))
        );

        if (!item.getOwnerId().equals(userId)) {
            throw new ForbiddenException(String.format("Попытка пользователя с id=%s обновить вещь пользователя id=%s",
                    userId, item.getOwnerId()));
        }

        final ItemDto recipient = toItemDto(item);
        final ItemDto patched = patchItemDto(recipient, itemDto);
        modelValidator.apply(patched);
        return toItemDto(itemRepository.save(fromItemDto(patched, userId)));
    }

    @Override
    public List<ItemDto> searchItemsBySubstring(String substring, int from, int size) {
        if (substring.isEmpty()) {
            return Collections.emptyList();
        }

        final Pageable pageable = PageRequest.of(from / size, size, SORT_BY_START_ASC);

        return toItemsDto(itemRepository.searchSubstring(substring, pageable));
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long userId) {

        final User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Комментарий несуществующего пользователя (id=%s)", userId))
        );

        final Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException(String.format("Комментарий к несуществующей вещи (id=%s)", itemId))
        );

        if (bookingRepository.findAllByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now()).isEmpty()) {
            throw new BadRequestException("Пользователь не брал вещь в аренду или не завершил ее");
        }
        return toCommentDto(commentRepository.save(fromCommentDto(commentDto, item, user)));
    }
}
