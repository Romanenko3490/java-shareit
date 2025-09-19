package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.enums.BookingState;

import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookIngInnerController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id")
                                 Long userId,
                                 @RequestBody NewBookingRequest request) {
        return bookingService.addBooking(userId, request);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto changeBookingStatus(@RequestHeader("X-Sharer-User-Id")
                                          Long userId,
                                          @PathVariable
                                          Long bookingId,
                                          @RequestParam
                                          Boolean approved) {
        return bookingService.updateBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id")
                                 Long userId,
                                 @PathVariable("bookingId")
                                 Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping()
    public List<BookingDto> getUserBookings(
            @RequestHeader("X-Sharer-User-Id")
            Long userId,
            @RequestParam(defaultValue = "ALL") String state) {
        BookingState bookingState = BookingState.from(state);
        return bookingService.getUserBookings(userId, bookingState);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnersBooking(
            @RequestHeader("X-Sharer-User-Id")
            Long userId,
            @RequestParam(defaultValue = "ALL") String state) {
        BookingState bookingState = BookingState.from(state);
        return bookingService.getOwnerBookings(userId, bookingState);
    }
}
