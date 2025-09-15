package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.base.BaseWebClient;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.enums.BookingState;

import java.util.List;

@Service
public class BookingClient extends BaseWebClient {
    private static final String API_PREFIX = "/bookings";
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    public BookingClient(@Value("${shareit-server.url}") String serverUrl) {
        super(serverUrl, API_PREFIX);
    }

    public BookingDto addBooking(Long userId, NewBookingRequest request) {
        return webClient.post()
                .header(USER_ID_HEADER, userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(BookingDto.class)
                .block();
    }

    public BookingDto updateBookingStatus(Long userId, Long bookingId, Boolean approved) {
        return webClient.patch()
                .uri("/" + bookingId + "?approved=" + approved)
                .header(USER_ID_HEADER, userId.toString())
                .retrieve()
                .bodyToMono(BookingDto.class)
                .block();
    }

    public BookingDto getBookingById(Long userId, Long bookingId) {
        return webClient.get()
                .uri("/" + bookingId)
                .header(USER_ID_HEADER, userId.toString())
                .retrieve()
                .bodyToMono(BookingDto.class)
                .block();
    }

    public List<BookingDto> getUserBookings(Long userId, BookingState state) {
        return webClient.get()
                .uri("?state=" + state)
                .header(USER_ID_HEADER, userId.toString())
                .retrieve()
                .bodyToFlux(BookingDto.class)
                .collectList()
                .block();
    }

    public List<BookingDto> getOwnerBookings(Long userId, BookingState state) {
        return webClient.get()
                .uri("/owner?state=" + state)
                .header(USER_ID_HEADER, userId.toString())
                .retrieve()
                .bodyToFlux(BookingDto.class)
                .collectList()
                .block();
    }
}
