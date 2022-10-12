package ru.practicum.shareit.booking.controller;

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
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.dto.BookingDto;
import ru.practicum.shareit.dto.BookingStatus;
import ru.practicum.shareit.dto.ItemDto;
import ru.practicum.shareit.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class BookingControllerTest {

    static ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    BookingService bookingService;

    MockMvc mockMvc;

    UserDto testUserDto = UserDto.of(1L, "Test user", "test@user.email");
    ItemDto testItemDto = ItemDto.of(1L, "Test item dto", "description", true, 1L);

    BookingDto testBookingDto = BookingDto.of(
            1L,
            LocalDateTime.now().plusMinutes(1),
            LocalDateTime.now().plusMinutes(2),
            BookingStatus.APPROVED,
            testUserDto,
            testItemDto,
            1L
    );

    @BeforeAll
    static void beforeAll() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void create() throws Exception {
        when(bookingService.createBooking(testBookingDto, 1L)).thenReturn(testBookingDto);

        mockMvc.perform(
                        post("/bookings")
                                .header("X-Sharer-User-Id", 1)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(testBookingDto))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(testBookingDto)));

        verify(bookingService, times(1)).createBooking(testBookingDto,1L);

    }

    @Test
    void approveReject() throws Exception {
        when(bookingService.approveRejectBooking(1L, 1L, true)).thenReturn(testBookingDto);

        mockMvc.perform(
                patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
        )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(testBookingDto)));

        verify(bookingService, times(1))
                .approveRejectBooking(1L, 1L, true);
    }

    @Test
    void getById() throws Exception {
        when(bookingService.getBookingById(1L, 1L)).thenReturn(testBookingDto);

        mockMvc.perform(
                        get("/bookings/1")
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(testBookingDto)));

        verify(bookingService, times(1)).getBookingById(1L,1L);
    }

    @Test
    void getByBookerIdAndState() throws Exception {
        when(bookingService.getAllBookingsByBookerAndState(1L, "ALL", 0, 10))
                .thenReturn(List.of(testBookingDto));

        mockMvc.perform(
                get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(testBookingDto))));

        verify(bookingService, times(1)).getAllBookingsByBookerAndState(1L, "ALL",
                0, 10);
    }

    @Test
    void getByOwnerIdAndState() throws Exception {
        when(bookingService.getAllBookingsByOwnerAndState(1L, "ALL", 0, 10))
                .thenReturn(List.of(testBookingDto));

        mockMvc.perform(
                        get("/bookings/owner")
                                .header("X-Sharer-User-Id", 1)
                                .param("state", "ALL")
                                .param("from", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(testBookingDto))));

        verify(bookingService, times(1)).getAllBookingsByOwnerAndState(1L, "ALL",
                0, 10);
    }
}