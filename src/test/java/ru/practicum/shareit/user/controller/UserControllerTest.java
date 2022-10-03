package ru.practicum.shareit.user.controller;

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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class UserControllerTest {

    static ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    UserService userService;

    MockMvc mockMvc;


    UserDto testUser = UserDto.of(1L, "Test user 1", "user1@email.test");

    List<UserDto> testUsers = List.of(
            testUser,
            UserDto.of(2L, "Test user 2", "user2@email.test"),
            UserDto.of(3L, "Test user 3", "user3@email.test")
    );

    @Test
    void getAll() throws Exception {
        when(userService.getAllUsers()).thenReturn(testUsers);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(testUsers)));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getById() throws Exception {
        when(userService.getUserById(any())).thenReturn(testUser);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(testUser)));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void create() throws Exception {
        when(userService.createUser(testUser)).thenReturn(testUser);

        mockMvc.perform(
                    post("/users")
                            .content(objectMapper.writeValueAsString(testUser))
                            .contentType("application/json")
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(objectMapper.writeValueAsString(testUser)));

        verify(userService, times(1)).createUser(testUser);
    }

    @Test
    void update() throws Exception {
        when(userService.updateUser(1L, testUser)).thenReturn(testUser);

        mockMvc.perform(
                        patch("/users/1")
                                .content(objectMapper.writeValueAsString(testUser))
                                .contentType("application/json")
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(objectMapper.writeValueAsString(testUser)));

        verify(userService, times(1)).updateUser(1L, testUser);
    }

    @Test
    void remove() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService, times(1)).removeUser(1L);
    }
}