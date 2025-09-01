package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;

import java.util.List;

/**
 * Контроллер для работы с бронированиями.
 * Обрабатывает HTTP-запросы, связанные с бронированием предметов.
 * Предоставляет endpoints для создания, обновления и получения информации о бронированиях.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;

    /**
     * Создает новое бронирование.
     *
     * @param userId  идентификатор пользователя из заголовка X-Sharer-User-Id
     * @param request данные для создания бронирования
     * @return созданное бронирование в формате DTO
     */
    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id")
                                 @Min(1) Long userId,
                                 @RequestBody @Valid NewBookingRequest request) {
        return bookingService.addBooking(userId, request);
    }

    /**
     * Изменяет статус бронирования (подтверждение/отклонение).
     *
     * @param userId    идентификатор пользователя из заголовка X-Sharer-User-Id
     * @param bookingId идентификатор бронирования
     * @param approved  флаг подтверждения (true - подтверждено, false - отклонено)
     * @return обновленное бронирование в формате DTO
     */
    @PatchMapping("/{bookingId}")
    public BookingDto changeBookingStatus(@RequestHeader("X-Sharer-User-Id")
                                          @Min(1) Long userId,
                                          @PathVariable
                                          @Min(1) Long bookingId,
                                          @RequestParam
                                          Boolean approved) {
        return bookingService.updateBookingStatus(userId, bookingId, approved);
    }

    /**
     * Получает информацию о конкретном бронировании по идентификатору.
     *
     * @param userId    идентификатор пользователя из заголовка X-Sharer-User-Id
     * @param bookingId идентификатор бронирования
     * @return информация о бронировании в формате DTO
     */
    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id")
                                 @Min(1) Long userId,
                                 @PathVariable("bookingId")
                                 @Min(1) Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    /**
     * Получает список всех бронирований текущего пользователя с возможностью фильтрации по статусу.
     *
     * @param userId идентификатор пользователя из заголовка X-Sharer-User-Id
     * @param state  статус бронирования для фильтрации (ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED)
     * @return список бронирований пользователя
     */
    @GetMapping()
    public List<BookingDto> getUserBookings(
            @RequestHeader("X-Sharer-User-Id")
            @Min(1) Long userId,
            @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getUserBookings(userId, state);
    }

    /**
     * Получает список бронирований для всех предметов, принадлежащих владельцу, с возможностью фильтрации по статусу.
     *
     * @param userId идентификатор владельца предметов из заголовка X-Sharer-User-Id
     * @param state  статус бронирования для фильтрации (ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED)
     * @return список бронирований предметов владельца
     */
    @GetMapping("/owner")
    public List<BookingDto> getOwnersBooking(
            @RequestHeader("X-Sharer-User-Id")
            @Min(1) Long userId,
            @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getOwnerBookings(userId, state);
    }
}