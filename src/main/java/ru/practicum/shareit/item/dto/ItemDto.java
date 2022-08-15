package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Value;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Value(staticConstructor = "of")
public class ItemDto {
    long id;
    String name;
    String description;
    Boolean available;
    Long request;
}
