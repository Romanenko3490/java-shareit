package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithReplaysDto;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.dto.ReplyDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemRequestDto itemRequestDto;
    private ItemRequestWithReplaysDto itemRequestWithReplaysDto;
    private NewRequest newRequest;
    private ReplyDto replyDto;

    @BeforeEach
    void setUp() {
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Need a hammer");
        itemRequestDto.setCreated(LocalDateTime.now().withNano(0));

        replyDto = new ReplyDto();
        replyDto.setItemId(1L);
        replyDto.setName("My Hammer");
        replyDto.setOwnerId(2L);

        itemRequestWithReplaysDto = new ItemRequestWithReplaysDto();
        itemRequestWithReplaysDto.setId(1L);
        itemRequestWithReplaysDto.setDescription("Need a hammer");
        itemRequestWithReplaysDto.setCreated(LocalDateTime.now().withNano(0));
        itemRequestWithReplaysDto.setItems(List.of(replyDto));

        newRequest = new NewRequest();
        newRequest.setDescription("Need a screwdriver");
    }

    @Test
    void addRequest_shouldReturnItemRequestDto() throws Exception {
        Long userId = 1L;
        when(itemRequestClient.addRequest(eq(userId), org.mockito.ArgumentMatchers.any(NewRequest.class))).thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", notNullValue()));

        verify(itemRequestClient, times(1)).addRequest(eq(userId), org.mockito.ArgumentMatchers.any(NewRequest.class));
    }

    @Test
    void addRequest_shouldReturnBadRequest_whenUserIdHeaderIsMissing() throws Exception {
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRequest)))
                .andExpect(status().isBadRequest()); // 400 Bad Request из-за отсутствия заголовка

        verify(itemRequestClient, never()).addRequest(anyLong(), org.mockito.ArgumentMatchers.any(NewRequest.class));
    }

    @Test
    void addRequest_shouldReturnBadRequest_whenUserIdIsInvalid() throws Exception {
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 0) // Невалидный ID
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRequest)))
                .andExpect(status().isBadRequest()); // 400 Bad Request из-за @Min(1)

        verify(itemRequestClient, never()).addRequest(anyLong(), org.mockito.ArgumentMatchers.any(NewRequest.class));
    }

    @Test
    void addRequest_shouldReturnBadRequest_whenRequestBodyIsInvalid() throws Exception {
        Long userId = 1L;
        NewRequest invalidRequest = new NewRequest();
        invalidRequest.setDescription(""); // Пустое описание

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest()); // 400 Bad Request из-за @NotBlank

        verify(itemRequestClient, never()).addRequest(anyLong(), org.mockito.ArgumentMatchers.any(NewRequest.class));
    }


    @Test
    void getAllRequests_shouldReturnListOfItemRequestWithReplaysDto() throws Exception {
        Long userId = 1L;
        List<ItemRequestWithReplaysDto> requestList = List.of(itemRequestWithReplaysDto);

        when(itemRequestClient.getUserRequestsWithReplays(eq(userId))).thenReturn(requestList);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestWithReplaysDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestWithReplaysDto.getDescription())))
                .andExpect(jsonPath("$[0].created", notNullValue()))
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].itemId", is(replyDto.getItemId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name", is(replyDto.getName())))
                .andExpect(jsonPath("$[0].items[0].ownerId", is(replyDto.getOwnerId()), Long.class));

        verify(itemRequestClient, times(1)).getUserRequestsWithReplays(eq(userId));
    }

    @Test
    void getAllRequests_shouldReturnBadRequest_whenUserIdHeaderIsMissing() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getUserRequestsWithReplays(anyLong());
    }

    @Test
    void getAllRequests_shouldReturnBadRequest_whenUserIdIsInvalid() throws Exception {
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 0))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getUserRequestsWithReplays(anyLong());
    }


    @Test
    void getAllRequestsPublic_shouldReturnListOfItemRequestDto() throws Exception {
        List<ItemRequestDto> requestList = List.of(itemRequestDto);

        when(itemRequestClient.getAllRequests()).thenReturn(requestList);

        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].created", notNullValue()));

        verify(itemRequestClient, times(1)).getAllRequests();
    }


    @Test
    void getRequest_shouldReturnItemRequestWithReplaysDto() throws Exception {
        Long requestId = 1L;
        when(itemRequestClient.getRequestById(eq(requestId))).thenReturn(itemRequestWithReplaysDto);

        mockMvc.perform(get("/requests/{requestId}", requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestWithReplaysDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestWithReplaysDto.getDescription())))
                .andExpect(jsonPath("$.created", notNullValue()))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].itemId", is(replyDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.items[0].name", is(replyDto.getName())))
                .andExpect(jsonPath("$.items[0].ownerId", is(replyDto.getOwnerId()), Long.class));

        verify(itemRequestClient, times(1)).getRequestById(eq(requestId));
    }

    @Test
    void getRequest_shouldReturnBadRequest_whenRequestIdIsInvalid() throws Exception {
        mockMvc.perform(get("/requests/{requestId}", 0))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getRequestById(anyLong());
    }
}