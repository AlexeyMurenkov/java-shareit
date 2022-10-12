package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.dto.CommentDto;
import ru.practicum.shareit.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemGetDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class ItemControllerTest {

    static ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    ItemService itemService;

    MockMvc mockMvc;

    ItemDto testItemDto = ItemDto.of(1L, "Test item dto", "description", true, 1L);
    CommentDto testCommentDto = CommentDto.of(1L, "comment", "Author", null);
    ItemGetDto testItemGetDto = ItemGetDto.of(
            1L,
            "Test item 1",
            "Test description 1",
            true,
            ItemGetDto.BookingDto.of(1L, 1L, null, null),
            ItemGetDto.BookingDto.of(2L, 1L, null, null),
            List.of(CommentDto.of(1L, "Test comment", "Author", null))
        );
    List<ItemGetDto> testItemsGetDto = List.of(
            testItemGetDto,
            ItemGetDto.of(
                    2L,
                    "Test item 2",
                    "Test description 2",
                    true,
                    ItemGetDto.BookingDto.of(1L, 1L, null, null),
                    ItemGetDto.BookingDto.of(2L, 1L, null, null),
                    List.of(CommentDto.of(2L, "Test comment", "Author", null))
            ),
            ItemGetDto.of(
                    3L,
                    "Test item 3",
                    "Test description 3",
                    true,
                    ItemGetDto.BookingDto.of(3L, 1L, null, null),
                    ItemGetDto.BookingDto.of(4L, 1L, null, null),
                    List.of(CommentDto.of(3L, "Test comment", "Author", null))
            )
    );

    @Test
    void getById() throws Exception {
        when(itemService.getItemById(1L, 1L)).thenReturn(testItemGetDto);

        mockMvc.perform(
                    get("/items/1")
                        .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(testItemGetDto)));

        verify(itemService, times(1)).getItemById(1L, 1L);
    }

    @Test
    void getByUserId() throws Exception {
        when(itemService.getItemsByUserId(1L, 0, 10)).thenReturn(testItemsGetDto);

        mockMvc.perform(
                        get("/items")
                                .header("X-Sharer-User-Id", 1)
                                .param("from", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(testItemsGetDto)));

        verify(itemService, times(1)).getItemsByUserId(1L, 0, 10);
    }

    @Test
    void create() throws Exception {
        when(itemService.createItem(testItemDto, 1L)).thenReturn(testItemDto);

        mockMvc.perform(
                        post("/items")
                                .header("X-Sharer-User-Id", 1)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(testItemDto))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(testItemDto)));

        verify(itemService, times(1)).createItem(testItemDto,1L);
    }

    @Test
    void update() throws Exception {
        when(itemService.updateItem(testItemDto, 1L, 1L)).thenReturn(testItemDto);

        mockMvc.perform(
                        patch("/items/1")
                                .header("X-Sharer-User-Id", 1)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(testItemDto))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(testItemDto)));

        verify(itemService, times(1)).updateItem(testItemDto, 1L, 1L);
    }

    @Test
    void searchBySubstring() throws Exception {
        when(itemService.searchItemsBySubstring(anyString(), anyInt(), anyInt())).thenReturn(List.of(testItemDto));

        mockMvc.perform(
                    get("/items/search")
                            .param("text", "TEST")
                            .param("from", "0")
                            .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(testItemDto))));

        verify(itemService, times(1)).searchItemsBySubstring(anyString(), anyInt(), anyInt());
    }

    @Test
    void createComment() throws Exception {
        when(itemService.createComment(testCommentDto, 1L, 1L)).thenReturn(testCommentDto);

        mockMvc.perform(
                        post("/items/1/comment")
                                .header("X-Sharer-User-Id", 1)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(testCommentDto))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(testCommentDto)));

        verify(itemService, times(1)).createComment(testCommentDto,1L, 1L);
    }
}