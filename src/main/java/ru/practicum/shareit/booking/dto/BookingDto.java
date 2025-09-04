package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.item.dto.ItemBookingDto;

import java.time.LocalDateTime;

/**
 * DTO для представления полной информации о бронировании.
 * Используется для передачи данных о бронировании между слоями приложения.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {
    /**
     * Уникальный идентификатор бронирования.
     * Не может быть null.
     */
    private Long id;

    /**
     * Дата и время начала бронирования.
     * Не может быть null.
     */
    private LocalDateTime start;

    /**
     * Дата и время окончания бронирования.
     * Не может быть null.
     */
    private LocalDateTime end;

    /**
     * Предмет, который был забронирован.
     * Не может быть null.
     */
    private ItemBookingDto item;

    /**
     * Пользователь, который осуществил бронирование.
     * Не может быть null.
     */
    private UserBookingDto booker;

    /**
     * Статус бронирования.
     * Может содержать значения: APPROVED, REJECTED, WAITING, CANCELED и т.д.
     */
    private BookingStatus status;
}
