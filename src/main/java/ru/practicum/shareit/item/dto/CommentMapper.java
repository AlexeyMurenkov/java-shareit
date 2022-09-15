package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.of(
                comment.getId(),
                comment.getText(),
                comment.getAuthor() != null ? comment.getAuthor().getName() : "",
                comment.getCreated()
        );
    }

    public static Comment fromCommentDto(CommentDto commentDto, Item item, User user) {
        return Comment.of(
                commentDto.getId(),
                commentDto.getText(),
                item,
                user,
                Optional.ofNullable(commentDto.getCreated()).orElse(LocalDateTime.now())
        );
    }

    public static List<CommentDto> toCommentsDto(List<Comment> comments) {
        return comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }
}
