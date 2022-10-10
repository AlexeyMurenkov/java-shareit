package ru.practicum.shareit.item.repository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class CommentRepositoryTest {

    UserRepository userRepository;
    ItemRepository itemRepository;
    CommentRepository commentRepository;

    User testUser = User.of(1L, "User 1", "user1@email.ru");

    Item testItem1 = Item.of(1L, "Item 1", "Item 1 description", true, 1L,
            null);
    Item testItem2 = Item.of(2L, "Item 2", "Item 2 description", true, 1L,
            null);

    Comment testComment1 = Comment.of(1L, "Test comment 1", testItem1, testUser, LocalDateTime.now());
    Comment testComment2 = Comment.of(2L, "Test comment 1", testItem1, testUser,
            LocalDateTime.now().minusHours(1));

    private static void assertEqualsComments(Comment comment1, Comment comment2) {
        assertEquals(comment1.getId(), comment2.getId(), "Возвращается неверный id комментария");
        assertEquals(comment1.getText(), comment2.getText(), "Возвращается неверный текст");
        assertEquals(comment1.getCreated(), comment2.getCreated(), "Возвращается неверное время создания");
    }

    @BeforeEach
    void beforeEachTest() {
        userRepository.save(testUser);
        itemRepository.save(testItem1);
        itemRepository.save(testItem2);
        commentRepository.save(testComment1);
        commentRepository.save(testComment2);
    }

    @Test
    @DirtiesContext
    void findAllByItemOrderByCreated() {
        assertTrue(commentRepository.findAllByItemOrderByCreated(testItem2).isEmpty(),
                "Для пользователя без коментариев возвращается непустой список");

        final List<Comment> comments = commentRepository.findAllByItemOrderByCreated(testItem1);

        assertEquals(2, comments.size(), "Возвращается неверное количество коментариев");
        assertEquals(testComment2.getId(), comments.get(0).getId(), "Неверный порядок сортировки");
        assertEqualsComments(testComment2, comments.get(0));
        assertEqualsComments(testComment1, comments.get(1));
    }
}