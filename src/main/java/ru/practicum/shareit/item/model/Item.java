package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Value;
import ru.practicum.shareit.requests.model.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Value(staticConstructor = "of")
public class Item {
    long id;
    @NotBlank
    String name;
    @NotBlank
    String description;
    @NotNull
    Boolean available;
    @Positive(message = "Владелец вещи должен быть задан")
    long ownerId;
    ItemRequest request;
}
