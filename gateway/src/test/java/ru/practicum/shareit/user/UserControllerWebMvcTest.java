package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Objects;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto userDto;
    private NewUserRequest newUserRequest;
    private UpdateUserRequest updateUserRequest;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");

        newUserRequest = new NewUserRequest();
        newUserRequest.setName("Test User");
        newUserRequest.setEmail("test@example.com");

        updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setName("Updated User");
    }

    @Test
    void createUser_shouldReturnUserDto() throws Exception {
        when(userClient.createUser(any(NewUserRequest.class))).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserRequest)))
                .andExpect(status().isOk()) // Проверяем статус 200 OK
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class)) // Проверяем поля в JSON ответе
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userClient, times(1)).createUser(any(NewUserRequest.class));
    }

    @Test
    void createUser_shouldReturnBadRequest_whenNameIsBlank() throws Exception {
        NewUserRequest invalidRequest = new NewUserRequest();
        invalidRequest.setName("");
        invalidRequest.setEmail("test@example.com");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(any(NewUserRequest.class));
    }

    @Test
    void createUser_shouldReturnBadRequest_whenEmailIsInvalid() throws Exception {
        NewUserRequest invalidRequest = new NewUserRequest();
        invalidRequest.setName("Test User");
        invalidRequest.setEmail("invalid-email"); // Невалидный email

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest()); // Ожидаем 400 Bad Request

        verify(userClient, never()).createUser(any(NewUserRequest.class));
    }

    @Test
    void updateUser_shouldReturnUserDto() throws Exception {
        Long userId = 1L;
        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setId(userId);
        updatedUserDto.setName("Updated User");
        updatedUserDto.setEmail(userDto.getEmail());

        when(userClient.updateUser(eq(userId), any(UpdateUserRequest.class))).thenReturn(updatedUserDto);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedUserDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updatedUserDto.getName())))
                .andExpect(jsonPath("$.email", is(updatedUserDto.getEmail())));

        verify(userClient, times(1)).updateUser(eq(userId), any(UpdateUserRequest.class));
    }

    @Test
    void updateUser_shouldReturnBadRequest_whenUserIdIsInvalid() throws Exception {
        mockMvc.perform(patch("/users/{userId}", 0)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(updateUserRequest))))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).updateUser(anyLong(), any(UpdateUserRequest.class));
    }

    @Test
    void getUser_shouldReturnUserDto() throws Exception {
        Long userId = 1L;
        when(userClient.getUser(eq(userId))).thenReturn(userDto);

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userClient, times(1)).getUser(eq(userId));
    }

    @Test
    void getUser_shouldReturnBadRequest_whenUserIdIsInvalid() throws Exception {
        mockMvc.perform(get("/users/{userId}", 0))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).getUser(anyLong());
    }

    @Test
    void deleteUser_shouldReturnNoContent() throws Exception {
        Long userId = 1L;
        doNothing().when(userClient).deleteUser(eq(userId));

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userClient, times(1)).deleteUser(eq(userId));
    }

    @Test
    void deleteUser_shouldReturnBadRequest_whenUserIdIsInvalid() throws Exception {
        mockMvc.perform(delete("/users/{userId}", 0))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).deleteUser(anyLong());
    }
}