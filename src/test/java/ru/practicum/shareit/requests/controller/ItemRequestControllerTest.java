package ru.practicum.shareit.requests.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class ItemRequestControllerTest {

    static ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    ItemRequestService itemRequestService;

    MockMvc mockMvc;

    UserDto testUserDto = UserDto.of(1L, "Test user", "test@user.email");

    List<ItemDto> testItemsDto = List.of(ItemDto.of(1L, "Test item dto", "description", true,
            1L));

    ItemRequestDto testItemRequestDto = ItemRequestDto.of(1L, "Request description", testUserDto,
            LocalDateTime.now(), testItemsDto);

    @BeforeAll
    static void beforeAll() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void create() throws Exception {
        when(itemRequestService.createItemRequest(testItemRequestDto, 1L)).thenReturn(testItemRequestDto);

        mockMvc.perform(
                        post("/requests")
                                .header("X-Sharer-User-Id", 1)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(testItemRequestDto))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(testItemRequestDto)));

        verify(itemRequestService, times(1)).createItemRequest(testItemRequestDto, 1L);
    }

    @Test
    void getByRequestor() throws Exception {
        when(itemRequestService.getItemRequestsByRequestorId(1L)).thenReturn(List.of(testItemRequestDto));

        mockMvc.perform(
                        get("/requests")
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(testItemRequestDto))));

        verify(itemRequestService, times(1)).getItemRequestsByRequestorId(1L);
    }

    @Test
    void getByNotRequestor() throws Exception {
        when(itemRequestService.getItemRequestsByNotRequestorId(2L, 0, 10)).thenReturn(List.of(testItemRequestDto));

        mockMvc.perform(
                        get("/requests/all")
                                .header("X-Sharer-User-Id", 2)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(testItemRequestDto))));

        verify(itemRequestService, times(1)).getItemRequestsByNotRequestorId(2L, 0,
                10);
    }

    @Test
    void getById() throws Exception {
        when(itemRequestService.getRequestById(1L, 1L)).thenReturn(testItemRequestDto);

        mockMvc.perform(
                        get("/requests/1")
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(testItemRequestDto)));

        verify(itemRequestService, times(1)).getRequestById(1L, 1L);
    }
}