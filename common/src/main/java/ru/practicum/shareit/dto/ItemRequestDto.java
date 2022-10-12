package ru.practicum.shareit.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Value;
import lombok.With;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Value(staticConstructor = "of")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemRequestDto {
    Long id;
    @NotBlank
    String description;
    @With
    UserDto requestor;
    @With
    LocalDateTime created;
    List<ItemDto> items;
}
