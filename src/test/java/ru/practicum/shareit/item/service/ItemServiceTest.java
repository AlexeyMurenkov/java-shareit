package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.ModelValidator;
import ru.practicum.shareit.common.exceptoins.BadRequestException;
import ru.practicum.shareit.common.exceptoins.ForbiddenException;
import ru.practicum.shareit.common.exceptoins.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemGetDto;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class ItemServiceTest {

    static LocalDateTime NOW_DATE_TIME = LocalDateTime.now();

    ItemRepository itemRepository = mock(ItemRepository.class);
    ItemRequestRepository itemRequestRepository = mock(ItemRequestRepository.class);
    UserRepository userRepository = mock(UserRepository.class);
    BookingRepository bookingRepository = mock(BookingRepository.class);
    CommentRepository commentRepository = mock(CommentRepository.class);

    ItemService itemService = new ItemServiceImpl(itemRepository, itemRequestRepository, userRepository,
            bookingRepository, commentRepository, new ModelValidator<>());

    ItemDto testItemDto = ItemDto.of(1L, "Test item", "description", true, 1L);
    CommentDto testCommentDto = CommentDto.of(1L, "comment", "Test user", NOW_DATE_TIME);

    User testUser = User.of(1L, "Test user", "user@test.email");
    ItemRequest testItemRequest = ItemRequest.of(1L, "description", testUser, NOW_DATE_TIME);
    Item testItem = Item.of(1L, "Test item", "description", true, 1L,
            testItemRequest);
    Item testItemWithOtherOwner = Item.of(1L, "Test item", "description", true, 2L,
            null);

    Booking testBooking = Booking.of(1L, NOW_DATE_TIME, NOW_DATE_TIME, testItem, testUser, BookingStatus.APPROVED);
    ItemGetDto testItemGetDto = ItemGetDto.of(
            1L,
            "Test item",
            "description",
            true,
            ItemGetDto.BookingDto.of(1L, 1L, NOW_DATE_TIME, NOW_DATE_TIME),
            ItemGetDto.BookingDto.of(1L, 1L, NOW_DATE_TIME, NOW_DATE_TIME),
            Collections.emptyList()
    );

    @Test
    void getItemWithInvalidUserId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable e = assertThrows(NotFoundException.class, () -> itemService.getItemById(1L, 1L),
                "Запрос вещи от имени несуществующего пользователя не вызывает исключения");
        assertEquals("Запрос вещи несуществующим пользователем (id=1)", e.getMessage(),
                "Неверное сообщение об ошибке");
    }

    @Test
    void getItemWithInvalidItemId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable e = assertThrows(NotFoundException.class, () -> itemService.getItemById(1L, 1L),
                "Запрос несуществующей вещи не вызывает исключения");
        assertEquals("Вещь с id=1 не найдена", e.getMessage(),
                "Неверное сообщение об ошибке");
    }

    @Test
    void getItemById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItem));
        when(bookingRepository.findFirstByItemAndEndBeforeOrderByEndDesc(ArgumentMatchers.any(Item.class),
                ArgumentMatchers.any(LocalDateTime.class))).thenReturn(Optional.of(testBooking));
        when(bookingRepository.findFirstByItemAndStartAfterOrderByStartDesc(ArgumentMatchers.any(Item.class),
                ArgumentMatchers.any(LocalDateTime.class))).thenReturn(Optional.of(testBooking));

        when(commentRepository.findAllByItemOrderByCreated(ArgumentMatchers.any(Item.class)))
                .thenReturn(Collections.emptyList());

        final ItemGetDto itemGetDto = itemService.getItemById(1L, 1L);
        assertNotNull(itemGetDto, "Возвращается пустая вещь");
        assertEquals(testItemGetDto, itemGetDto, "Возвращается неверная вещь");
    }

    @Test
    void getItemWithOtherOwner() {
        final ItemGetDto item = ItemGetDto.of(
                1L,
                "Test item",
                "description",
                true,
                null,
                null,
                Collections.emptyList()
        );

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItemWithOtherOwner));
        when(commentRepository.findAllByItemOrderByCreated(ArgumentMatchers.any(Item.class)))
                .thenReturn(Collections.emptyList());

        final ItemGetDto itemGetDto = itemService.getItemById(1L, 1L);
        assertNotNull(itemGetDto, "Возвращается пустая вещь");
        assertEquals(item, itemGetDto, "Возвращается неверная вещь");
    }

    @Test
    void getItemsByUserIdWithInvalidUserId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable e = assertThrows(NotFoundException.class, () -> itemService.getItemsByUserId(1L, 0,
                        10),"Запрос вещи от имени несуществующего пользователя не вызывает исключения");
        assertEquals("Запрос вещи несуществующим пользователем (id=1)", e.getMessage(),
                "Неверное сообщение об ошибке");
    }

    @Test
    void getItemsByUserId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(itemRepository.findAllByOwnerId(anyLong(), any(Pageable.class))).thenReturn(List.of(testItem));
        when(bookingRepository.findFirstByItemAndEndBeforeOrderByEndDesc(ArgumentMatchers.any(Item.class),
                ArgumentMatchers.any(LocalDateTime.class))).thenReturn(Optional.of(testBooking));
        when(bookingRepository.findFirstByItemAndStartAfterOrderByStartDesc(ArgumentMatchers.any(Item.class),
                ArgumentMatchers.any(LocalDateTime.class))).thenReturn(Optional.of(testBooking));

        when(commentRepository.findAllByItemOrderByCreated(ArgumentMatchers.any(Item.class)))
                .thenReturn(Collections.emptyList());

        final List<ItemGetDto> itemsGetDto = itemService.getItemsByUserId(1L, 0, 10);
        assertNotNull(itemsGetDto, "Не возвращается список вещей");
        assertIterableEquals(List.of(testItemGetDto), itemsGetDto, "Возвращается неверная вещь");
    }

    @Test
    void createItemByInvalidUser() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        Throwable e = assertThrows(NotFoundException.class, () -> itemService.createItem(testItemDto, 1L),
                "Запрос вещи от имени несуществующего пользователя не вызывает исключения");
        assertEquals("Попытка создания вещи несуществующим пользователем (id=1)", e.getMessage(),
                "Неверное сообщение об ошибке");
    }

    @Test
    void createItemWithInvalidRequest() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable e = assertThrows(NotFoundException.class, () -> itemService.createItem(testItemDto, 1L),
                "Запрос вещи с неверным запросом не вызывает исключения");
        assertEquals("Создание вещи для несуществующего запроса", e.getMessage(),
                "Неверное сообщение об ошибке");
    }

    @Test
    void createItemWithNullRequest() {
        final ItemDto testItemDto = ItemDto.of(1L, "Test item", "description", true,
                null);
        final Item testItem = Item.of(1L, "Test item", "description", true, 1L,
                null);

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.empty());
        when(itemRepository.save(any(Item.class))).thenReturn(testItem);

        final ItemDto createdItem = itemService.createItem(testItemDto, 1L);

        assertNotNull(createdItem, "Не создается вещь");
        assertEquals(testItemDto, createdItem, "Создается неверная вещь");
    }

    @Test
    void createItem() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(testItemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(testItem);

        final ItemDto createdItem = itemService.createItem(testItemDto, 1L);

        assertNotNull(createdItem, "Не создается вещь");
        assertEquals(testItemDto, createdItem, "Создается неверная вещь");
    }


    @Test
    void updateItemByInvalidUser() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        Throwable e = assertThrows(NotFoundException.class, () -> itemService.updateItem(testItemDto, 1L,
                        1L),"Запрос вещи от имени несуществующего пользователя не вызывает исключения");
        assertEquals("Попытка создания вещи несуществующим пользователем (id=1)", e.getMessage(),
                "Неверное сообщение об ошибке");
    }

    @Test
    void updateItemWithInvalidItem() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable e = assertThrows(NotFoundException.class, () -> itemService.updateItem(testItemDto, 1L,
                1L),"Запрос несуществующей вещи не вызывает исключения");
        assertEquals("Попытка обновления несуществующей вещи (id=1)", e.getMessage(),
                "Неверное сообщение об ошибке");
    }

    @Test
    void updateItemWithOtherOwner() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItemWithOtherOwner));

        Throwable e = assertThrows(ForbiddenException.class, () -> itemService.updateItem(testItemDto, 1L,
                1L),"Запрос несуществующей вещи не вызывает исключения");
        assertEquals("Попытка пользователя с id=1 обновить вещь пользователя id=2", e.getMessage(),
                "Неверное сообщение об ошибке");
    }

    @Test
    void updateItem() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItem));
        when(itemRepository.save(any(Item.class))).thenReturn(testItem);

        final ItemDto itemDto = itemService.updateItem(testItemDto, 1L, 1L);

        assertNotNull(itemDto, "Не обновляется вещь");
        assertEquals(testItemDto, itemDto, "Неверно обновляется вещь");
    }

    @Test
    void searchItemsBySubstringByEmptyString() {
        when(itemRepository.searchSubstring(anyString(), any(Pageable.class))).thenReturn(Collections.emptyList());

        final List<ItemDto> itemsDto = itemService.searchItemsBySubstring("", 0, 10);
        assertNotNull(itemsDto, "Не возвращаются результаты поиска");
        assertTrue(itemsDto.isEmpty(), "Возвращается непустой список");
    }

    @Test
    void searchItemsBySubstring() {
        when(itemRepository.searchSubstring(anyString(), any(Pageable.class))).thenReturn(List.of(testItem));

        final List<ItemDto> itemsDto = itemService.searchItemsBySubstring("tem", 0, 10);
        assertNotNull(itemsDto, "Не возвращаются результаты поиска");
        assertIterableEquals(List.of(testItemDto), itemsDto, "Возвращаются неверные результаты поиска");
    }

    @Test
    void createCommentByInvalidUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable e = assertThrows(NotFoundException.class, () -> itemService.createComment(testCommentDto, 1L,
                1L),"Комментарий от имени несуществующего пользователя не вызывает исключения");
        assertEquals("Комментарий несуществующего пользователя (id=1)", e.getMessage(),
                "Неверное сообщение об ошибке");
    }

    @Test
    void createCommentWithInvalidItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable e = assertThrows(NotFoundException.class, () -> itemService.createComment(testCommentDto, 1L,
                1L),"Комментарий к несуществующей вещи не вызывает исключения");
        assertEquals("Комментарий к несуществующей вещи (id=1)", e.getMessage(),
                "Неверное сообщение об ошибке");
    }

    @Test
    void createCommentWithBadRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItem));
        when(bookingRepository.findAllByItemIdAndBookerIdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        Throwable e = assertThrows(BadRequestException.class, () -> itemService.createComment(testCommentDto, 1L,
                1L),"Комментарий к вещи, которую пользователь не брал не вызывает исключения");
        assertEquals("Пользователь не брал вещь в аренду или не завершил ее", e.getMessage(),
                "Неверное сообщение об ошибке");
    }

    @Test
    void createComment() {
        final Comment comment = Comment.of(1L, "comment", testItem, testUser, NOW_DATE_TIME);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItem));
        when(bookingRepository.findAllByItemIdAndBookerIdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(testBooking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        final CommentDto commentDto = itemService.createComment(testCommentDto, 1L, 1L);
        assertNotNull(commentDto, "Возвращается пустой комментарий");
        assertEquals(testCommentDto, commentDto, "Возврщается неверный комментарий");
    }

}