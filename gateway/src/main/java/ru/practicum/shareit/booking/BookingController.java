package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.enums.BookingState;

import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id")
                                 @Min(1) Long userId,
                                 @RequestBody
                                 @Valid NewBookingRequest request) {
        return bookingClient.addBooking(userId, request);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto changeBookingStatus(@RequestHeader("X-Sharer-User-Id")
                                          @Min(1) Long userId,
                                          @PathVariable
                                          @Min(1) Long bookingId,
                                          @RequestParam
                                          Boolean approved) {
        return bookingClient.updateBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id")
                                 @Min(1) Long userId,
                                 @PathVariable("bookingId")
                                 @Min(1) Long bookingId) {
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping()
    public List<BookingDto> getUserBookings(
            @RequestHeader("X-Sharer-User-Id")
            @Min(1) Long userId,
            @RequestParam(defaultValue = "ALL") String state) {
        BookingState bookingState = BookingState.from(state);
        return bookingClient.getUserBookings(userId, bookingState);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnersBooking(
            @RequestHeader("X-Sharer-User-Id")
            @Min(1) Long userId,
            @RequestParam(defaultValue = "ALL") String state) {
        BookingState bookingState = BookingState.from(state);
        return bookingClient.getOwnerBookings(userId, bookingState);
    }
}
