package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.dto.comments.CommentDto;
import ru.practicum.shareit.item.dto.comments.NewCommentRequest;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ItemController.class)
class ItemControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemClient itemClient;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemDto itemDto;
    private ItemLastNextBookingsAndCommentsDto itemWithBookingsDto;
    private ItemWithBookingsDto itemWithBookingsCollectionDto;
    private CommentDto commentDto;
    private NewItemRequest newItemRequest;
    private UpdateItemRequest updateItemRequest;
    private NewCommentRequest newCommentRequest;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Item Name");
        itemDto.setDescription("Item Description");
        itemDto.setOwnerId(1L);
        itemDto.setAvailable(true);
        itemDto.setRequestId(null);

        itemWithBookingsDto = new ItemLastNextBookingsAndCommentsDto();
        itemWithBookingsDto.setId(1L);
        itemWithBookingsDto.setName("Item Name");
        itemWithBookingsDto.setDescription("Item Description");
        itemWithBookingsDto.setAvailable(true);
        itemWithBookingsDto.setLastBooking(null);
        itemWithBookingsDto.setNextBooking(null);
        itemWithBookingsDto.setComments(List.of());

        itemWithBookingsCollectionDto = new ItemWithBookingsDto();
        itemWithBookingsCollectionDto.setId(1L);
        itemWithBookingsCollectionDto.setName("Item Name");
        itemWithBookingsCollectionDto.setDescription("Item Description");
        itemWithBookingsCollectionDto.setAvailable(true);
        itemWithBookingsCollectionDto.setBookings(List.of());
        itemWithBookingsCollectionDto.setComments(List.of());

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Great item!");
        commentDto.setAuthorName("Commenter");
        commentDto.setCreated(LocalDateTime.now().withNano(0));

        newItemRequest = new NewItemRequest();
        newItemRequest.setName("New Item");
        newItemRequest.setDescription("New Item Description");
        newItemRequest.setAvailable(true);
        newItemRequest.setRequestId(null);

        updateItemRequest = new UpdateItemRequest();
        updateItemRequest.setName("Updated Item Name");

        newCommentRequest = new NewCommentRequest();
        newCommentRequest.setText("Excellent tool!");
    }

    @Test
    void createItem_shouldReturnItemDto() throws Exception {
        Long userId = 1L;
        when(itemClient.addItem(eq(userId), org.mockito.ArgumentMatchers.any(NewItemRequest.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.ownerId", is(itemDto.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.requestId", itemDto.getRequestId() == null ? nullValue() : is(itemDto.getRequestId()), Long.class));

        verify(itemClient, times(1)).addItem(eq(userId), org.mockito.ArgumentMatchers.any(NewItemRequest.class));
    }

    @Test
    void createItem_shouldReturnBadRequest_whenUserIdHeaderIsMissing() throws Exception {
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemRequest)))
                .andExpect(status().isBadRequest()); // 400 Bad Request из-за отсутствия заголовка

        verify(itemClient, never()).addItem(anyLong(), org.mockito.ArgumentMatchers.any(NewItemRequest.class));
    }

    @Test
    void createItem_shouldReturnBadRequest_whenUserIdIsInvalid() throws Exception {
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 0) // Невалидный ID
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemRequest)))
                .andExpect(status().isBadRequest()); // 400 Bad Request из-за @Min(1)

        verify(itemClient, never()).addItem(anyLong(), org.mockito.ArgumentMatchers.any(NewItemRequest.class));
    }

    @Test
    void updateItem_shouldReturnItemDto() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto updatedItemDto = new ItemDto();
        updatedItemDto.setId(itemId);
        updatedItemDto.setName("Updated Item Name");
        updatedItemDto.setDescription(itemDto.getDescription()); // Остальные поля без изменений
        updatedItemDto.setOwnerId(itemDto.getOwnerId());
        updatedItemDto.setAvailable(itemDto.getAvailable());
        updatedItemDto.setRequestId(itemDto.getRequestId());

        when(itemClient.updateItem(eq(userId), eq(itemId), org.mockito.ArgumentMatchers.any(UpdateItemRequest.class))).thenReturn(updatedItemDto);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateItemRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updatedItemDto.getName())))
                .andExpect(jsonPath("$.description", is(updatedItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(updatedItemDto.getAvailable())));

        verify(itemClient, times(1)).updateItem(eq(userId), eq(itemId), org.mockito.ArgumentMatchers.any(UpdateItemRequest.class));
    }

    @Test
    void updateItem_shouldReturnBadRequest_whenUserIdHeaderIsMissing() throws Exception {
        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateItemRequest)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).updateItem(anyLong(), anyLong(), org.mockito.ArgumentMatchers.any(UpdateItemRequest.class));
    }

    @Test
    void updateItem_shouldReturnBadRequest_whenItemIdIsInvalid() throws Exception {
        mockMvc.perform(patch("/items/{itemId}", 0)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateItemRequest)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).updateItem(anyLong(), anyLong(), org.mockito.ArgumentMatchers.any(UpdateItemRequest.class));
    }

    @Test
    void getItem_shouldReturnItemWithBookingsAndCommentsDto() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        when(itemClient.getItem(eq(userId), eq(itemId))).thenReturn(itemWithBookingsDto);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemWithBookingsDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemWithBookingsDto.getName())))
                .andExpect(jsonPath("$.available", is(itemWithBookingsDto.getAvailable())))
                .andExpect(jsonPath("$.lastBooking", itemWithBookingsDto.getLastBooking() == null ? nullValue() : notNullValue()))
                .andExpect(jsonPath("$.nextBooking", itemWithBookingsDto.getNextBooking() == null ? nullValue() : notNullValue()))
                .andExpect(jsonPath("$.comments", is(itemWithBookingsDto.getComments())));

        verify(itemClient, times(1)).getItem(eq(userId), eq(itemId));
    }

    @Test
    void getItem_shouldReturnBadRequest_whenUserIdHeaderIsMissing() throws Exception {
        mockMvc.perform(get("/items/{itemId}", 1L))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getItem(anyLong(), anyLong());
    }

    @Test
    void getItem_shouldReturnBadRequest_whenItemIdIsInvalid() throws Exception {
        mockMvc.perform(get("/items/{itemId}", 0)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getItem(anyLong(), anyLong());
    }

    @Test
    void getUserItems_shouldReturnCollectionOfItemWithBookingsDto() throws Exception {
        Long userId = 1L;
        Collection<ItemWithBookingsDto> itemCollection = List.of(itemWithBookingsCollectionDto);

        when(itemClient.getUserItems(eq(userId))).thenReturn(itemCollection);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemWithBookingsCollectionDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemWithBookingsCollectionDto.getName())))
                .andExpect(jsonPath("$[0].available", is(itemWithBookingsCollectionDto.getAvailable())))
                .andExpect(jsonPath("$[0].bookings", is(itemWithBookingsCollectionDto.getBookings())))
                .andExpect(jsonPath("$[0].comments", is(itemWithBookingsCollectionDto.getComments())));

        verify(itemClient, times(1)).getUserItems(eq(userId));
    }

    @Test
    void getUserItems_shouldReturnBadRequest_whenUserIdHeaderIsMissing() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getUserItems(anyLong());
    }

    @Test
    void searchItemsByText_shouldReturnCollectionOfItemDto() throws Exception {
        String searchText = "hammer";
        Collection<ItemDto> itemCollection = List.of(itemDto);

        when(itemClient.searchItemsByText(eq(searchText))).thenReturn(itemCollection);

        mockMvc.perform(get("/items/search")
                        .param("text", searchText))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));

        verify(itemClient, times(1)).searchItemsByText(eq(searchText));
    }

    @Test
    void searchItemsByText_shouldReturnBadRequest_whenTextParamIsBlank() throws Exception {
        mockMvc.perform(get("/items/search")
                        .param("text", ""))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/items/search")
                        .param("text", "   "))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/items/search"))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).searchItemsByText(anyString());
    }


    @Test
    void addComment_shouldReturnCommentDto() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        when(itemClient.addComment(eq(userId), eq(itemId), org.mockito.ArgumentMatchers.any(NewCommentRequest.class))).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCommentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", notNullValue())); // Проверяем, что дата не null

        verify(itemClient, times(1)).addComment(eq(userId), eq(itemId), org.mockito.ArgumentMatchers.any(NewCommentRequest.class));
    }

    @Test
    void addComment_shouldReturnBadRequest_whenUserIdHeaderIsMissing() throws Exception {
        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCommentRequest)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addComment(anyLong(), anyLong(), org.mockito.ArgumentMatchers.any(NewCommentRequest.class));
    }

    @Test
    void addComment_shouldReturnBadRequest_whenItemIdIsInvalid() throws Exception {
        mockMvc.perform(post("/items/{itemId}/comment", 0) // Невалидный ID
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCommentRequest)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addComment(anyLong(), anyLong(), org.mockito.ArgumentMatchers.any(NewCommentRequest.class));
    }

    @Test
    void addComment_shouldReturnBadRequest_whenRequestBodyIsInvalid() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        NewCommentRequest invalidRequest = new NewCommentRequest();
        invalidRequest.setText(""); // Пустой текст

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest()); // 400 Bad Request из-за @NotBlank

        verify(itemClient, never()).addComment(anyLong(), anyLong(), org.mockito.ArgumentMatchers.any(NewCommentRequest.class));
    }
}