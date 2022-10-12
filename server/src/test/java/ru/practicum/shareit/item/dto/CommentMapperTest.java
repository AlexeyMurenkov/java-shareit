package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class CommentMapperTest {

    static LocalDateTime NOW_DATE_TIME = LocalDateTime.now();
    Item testItem = Item.of(1L, "Item 1", "Item 1 description", true, 2L,
            null);
    User testUser = User.of(1L, "Test user", "test@user.email");
    Comment testComment = Comment.of(1L, "text of comment", testItem, testUser, NOW_DATE_TIME);
    CommentDto testCommentDto = CommentDto.of(1L, "text of comment", "Test user", NOW_DATE_TIME);

    @Test
    void toCommentDto() {
        final CommentDto commentDto = CommentMapper.toCommentDto(testComment);
        assertNotNull(commentDto, "Возвращается пустой DTO комментария");
        assertEquals(testCommentDto, commentDto, "Возвращается неверный DTO комментария");
    }

    @Test
    void fromCommentDto() {
        final Comment comment = CommentMapper.fromCommentDto(testCommentDto, testItem, testUser);
        assertNotNull(comment, "Не возвращается коимментарий по DTO");
        assertEquals(testComment.getId(), comment.getId(), "Возвращается неверный id комментария");
        assertEquals(testComment.getText(), comment.getText(), "Возвращается неверный текст комментария");
    }

    @Test
    void toCommentsDto() {
        final List<CommentDto> commentsDto = List.of(CommentMapper.toCommentDto(testComment));
        assertNotNull(commentsDto, "Не возвращается список DTO комментариев");
        assertIterableEquals(List.of(testCommentDto), commentsDto, "Возвращается неверный список DTO");
    }

    @Test
    void defaultConstructor() {
        final CommentMapper commentMapper = new CommentMapper();
        assertNotNull(commentMapper, "Объект маппера комментариев не создается");
        assertInstanceOf(CommentMapper.class, commentMapper, "Создается объект неверного класса");
    }
}