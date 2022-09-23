package ru.practicum.shareit.requests.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Value;
import lombok.With;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

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
