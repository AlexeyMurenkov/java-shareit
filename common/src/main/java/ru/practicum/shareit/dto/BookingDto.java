package ru.practicum.shareit.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Value;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Value(staticConstructor = "of")
public class BookingDto {
    Long id;
    @FutureOrPresent(message = "Начало аренды не может быть в прошлом")
    @NotNull(message = "Время начала аренды должно быть заполнено")
    LocalDateTime start;
    @FutureOrPresent(message = "Окончание аренды не может быть в прошлом")
    @NotNull(message = "Время окончания аренды должно быть заполнено")
    LocalDateTime end;
    BookingStatus status;
    UserDto booker;
    ItemDto item;
    @NotNull(message = "id вещи для аренды не должен быть пустым")
    Long itemId;
}
