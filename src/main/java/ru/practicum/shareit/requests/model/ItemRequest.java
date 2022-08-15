package ru.practicum.shareit.requests.model;

import lombok.Value;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Value(staticConstructor = "of")
public class ItemRequest {
    long id;
    String description;
    User requestor;
    LocalDateTime created;
}