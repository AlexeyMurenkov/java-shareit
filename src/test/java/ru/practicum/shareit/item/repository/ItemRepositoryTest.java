package ru.practicum.shareit.item.repository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class ItemRepositoryTest {

    ItemRepository itemRepository;
    UserRepository userRepository;
    ItemRequestRepository itemRequestRepository;

    User testUser1 = User.of(1L, "User 1", "user1@email.ru");
    User testUser2 = User.of(2L, "User 2", "user2@email.ru");

    ItemRequest testItemRequest = ItemRequest.of(1L, "Test request", testUser2, LocalDateTime.now());

    Item testItem1 = Item.of(1L, "Item 1", "Item 1 description", true, 2L,
            null);
    Item testItem2 = Item.of(2L, "Item 2", "Item 2 description", true, 2L,
            testItemRequest);

    private static void assertEqualsItems(Item item1, Item item2) {
        assertEquals(item1.getId(), item2.getId(), "Возвращается неверный id вещи");
        assertEquals(item1.getName(), item2.getName(), "Возвращается неверный название");
        assertEquals(item1.getDescription(), item2.getDescription(), "Возвращается неверное описание");
        assertEquals(item1.isAvailable(), item2.isAvailable(), "Возвращается неверный признак доступности");
        assertEquals(item1.getOwnerId(), item2.getOwnerId(), "Возвращается неверный владелец");
    }

    @BeforeEach
    void beforeEachTest() {
        userRepository.save(testUser1);
        userRepository.save(testUser2);
        itemRequestRepository.save(testItemRequest);
        itemRepository.save(testItem1);
        itemRepository.save(testItem2);
    }

    @Test
    @DirtiesContext
    void findAllByOwnerId() {
        assertTrue(itemRepository.findAllByOwnerId(0L, Pageable.unpaged()).isEmpty(),
                "Для несуществующего пользователя возвращается непустой список вещей");
        assertTrue(itemRepository.findAllByOwnerId(1L, Pageable.unpaged()).isEmpty(),
                "Для пользователя без вещей возвращается непустой список вещей");

        final List<Item> items = itemRepository.findAllByOwnerId(2L, Pageable.unpaged());

        assertEquals(2, items.size(), "Возвращается неверынй размер списка");
        assertEqualsItems(testItem1, items.get(0));
        assertEqualsItems(testItem2, items.get(1));
    }

    @Test
    @DirtiesContext
    void searchSubstring() {
        assertTrue(itemRepository.searchSubstring("UNFOUND", Pageable.unpaged()).isEmpty(),
                "Для запроса несуществующей вещи возвращается непустой список вещей");

        final List<Item> itemsByDescr = itemRepository.searchSubstring("1 descr", Pageable.unpaged());
        assertEquals(1, itemsByDescr.size(), "Возвращается неверынй размер списка");
        assertEqualsItems(testItem1, itemsByDescr.get(0));

        final List<Item> itemsByName = itemRepository.searchSubstring("tem 2", Pageable.unpaged());
        assertEquals(1, itemsByName.size(), "Возвращается неверынй размер списка");
        assertEqualsItems(testItem2, itemsByName.get(0));
    }

    @Test
    @DirtiesContext
    void findAllByRequest() {
        final List<Item> items = itemRepository.findAllByRequest(testItemRequest);
        assertEquals(1, items.size(), "Возвращается неверынй размер списка");
        assertEqualsItems(testItem2, items.get(0));
    }
}