package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.enums.BookingState;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.user.dto.SimpleUserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingClient bookingClient;

    @Autowired
    private ObjectMapper objectMapper;

    private BookingDto bookingDto;
    private NewBookingRequest newBookingRequest;

    @BeforeEach
    void setUp() {
        SimpleUserDto bookerDto = new SimpleUserDto();
        bookerDto.setId(2L);
        bookerDto.setName("Booker Name");

        ItemBookingDto itemDto = new ItemBookingDto();
        itemDto.setId(1L);
        itemDto.setName("Item Name");

        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setItem(itemDto);
        bookingDto.setBooker(bookerDto);
        bookingDto.setStatus(BookingStatus.WAITING);

        newBookingRequest = new NewBookingRequest();
        newBookingRequest.setItemId(1L);
        newBookingRequest.setStart(bookingDto.getStart());
        newBookingRequest.setEnd(bookingDto.getEnd());
    }


    @Test
    void addBooking_shouldReturnBadRequest_whenUserIdHeaderIsMissing() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBookingRequest)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).addBooking(anyLong(), any(NewBookingRequest.class));
    }

    @Test
    void addBooking_shouldReturnBadRequest_whenUserIdIsInvalid() throws Exception {
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 0)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBookingRequest)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).addBooking(anyLong(), any(NewBookingRequest.class));
    }

    @Test
    void changeBookingStatus_shouldReturnBookingDto() throws Exception {
        Long userId = 1L;
        Long bookingId = 1L;
        Boolean approved = true;
        bookingDto.setStatus(BookingStatus.APPROVED);
        when(bookingClient.updateBookingStatus(eq(userId), eq(bookingId), eq(approved))).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", approved.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name())));

        verify(bookingClient, times(1)).updateBookingStatus(eq(userId), eq(bookingId), eq(approved));
    }

    @Test
    void changeBookingStatus_shouldReturnBadRequest_whenUserIdHeaderIsMissing() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).updateBookingStatus(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void changeBookingStatus_shouldReturnBadRequest_whenBookingIdIsInvalid() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}", 0) // Невалидный ID
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isBadRequest()); // 400 Bad Request из-за @Min(1)

        verify(bookingClient, never()).updateBookingStatus(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void changeBookingStatus_shouldReturnBadRequest_whenApprovedParamIsMissing() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).updateBookingStatus(anyLong(), anyLong(), anyBoolean());
    }


    @Test
    void getBooking_shouldReturnBookingDto() throws Exception {
        Long userId = 1L;
        Long bookingId = 1L;
        when(bookingClient.getBookingById(eq(userId), eq(bookingId))).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));

        verify(bookingClient, times(1)).getBookingById(eq(userId), eq(bookingId));
    }

    @Test
    void getBooking_shouldReturnBadRequest_whenUserIdHeaderIsMissing() throws Exception {
        mockMvc.perform(get("/bookings/{bookingId}", 1L))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getBookingById(anyLong(), anyLong());
    }

    @Test
    void getBooking_shouldReturnBadRequest_whenBookingIdIsInvalid() throws Exception {
        mockMvc.perform(get("/bookings/{bookingId}", 0) // Невалидный ID
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest()); // 400 Bad Request из-за @Min(1)

        verify(bookingClient, never()).getBookingById(anyLong(), anyLong());
    }


    @Test
    void getUserBookings_shouldReturnListOfBookingDtos() throws Exception {
        Long userId = 1L;
        String stateParam = "WAITING";
        BookingState state = BookingState.WAITING;
        List<BookingDto> bookingList = List.of(bookingDto); // Список из одного элемента

        when(bookingClient.getUserBookings(eq(userId), eq(state))).thenReturn(bookingList);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", stateParam))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class));

        verify(bookingClient, times(1)).getUserBookings(eq(userId), eq(state));
    }

    @Test
    void getUserBookings_shouldUseDefaultState_whenStateParamIsMissing() throws Exception {
        Long userId = 1L;
        String defaultStateParam = "ALL"; // Дефолтное значение из контроллера
        BookingState defaultState = BookingState.ALL;
        List<BookingDto> bookingList = List.of(); // Пустой список

        when(bookingClient.getUserBookings(eq(userId), eq(defaultState))).thenReturn(bookingList);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId))
                // Не передаем параметр state
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(bookingClient, times(1)).getUserBookings(eq(userId), eq(defaultState));
    }

    @Test
    void getUserBookings_shouldReturnBadRequest_whenUserIdHeaderIsMissing() throws Exception {
        mockMvc.perform(get("/bookings")
                        .param("state", "ALL"))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getUserBookings(anyLong(), any(BookingState.class));
    }

    @Test
    void getUserBookings_shouldReturnBadRequest_whenStateParamIsInvalid() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "INVALID_STATE"))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getUserBookings(anyLong(), any(BookingState.class));
    }


    @Test
    void getOwnersBooking_shouldReturnListOfBookingDtos() throws Exception {
        Long userId = 1L; // Владелец вещи
        String stateParam = "REJECTED";
        BookingState state = BookingState.REJECTED;
        List<BookingDto> bookingList = List.of(bookingDto);

        when(bookingClient.getOwnerBookings(eq(userId), eq(state))).thenReturn(bookingList);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", stateParam))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class));

        verify(bookingClient, times(1)).getOwnerBookings(eq(userId), eq(state));
    }

    @Test
    void getOwnersBooking_shouldUseDefaultState_whenStateParamIsMissing() throws Exception {
        Long userId = 1L;
        String defaultStateParam = "ALL";
        BookingState defaultState = BookingState.ALL;
        List<BookingDto> bookingList = List.of();

        when(bookingClient.getOwnerBookings(eq(userId), eq(defaultState))).thenReturn(bookingList);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(bookingClient, times(1)).getOwnerBookings(eq(userId), eq(defaultState));
    }

    @Test
    void getOwnersBooking_shouldReturnBadRequest_whenUserIdHeaderIsMissing() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL"))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getOwnerBookings(anyLong(), any(BookingState.class));
    }

    @Test
    void getOwnersBooking_shouldReturnBadRequest_whenStateParamIsInvalid() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ANOTHER_INVALID_STATE"))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getOwnerBookings(anyLong(), any(BookingState.class));
    }
}