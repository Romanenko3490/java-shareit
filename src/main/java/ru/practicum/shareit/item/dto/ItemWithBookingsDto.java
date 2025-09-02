package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.comments.dto.CommentDto;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * DTO для представления предмета с информацией о бронированиях.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemWithBookingsDto {
    /**
     * Идентификатор предмета.
     */
    private Long id;

    /**
     * Название предмета.
     */
    private String name;

    /**
     * Описание предмета.
     */
    private String description;

    /**
     * Доступность предмета для аренды.
     */
    private Boolean available;

    /**
     * Последнее бронирование (прошлое)
     */
    private List<BookingShortDto> bookings;
    /**
     * Комментарии к предмету
     */
    private List<CommentDto> comments;

    public BookingShortDto getLastBooking() {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }
        return bookings.stream()
                .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(BookingShortDto::getEnd))
                .orElse(null);
    }

    public BookingShortDto getNextBooking() {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }
        return bookings.stream()
                .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(BookingShortDto::getStart))
                .orElse(null);
    }
}
