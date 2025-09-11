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
 * DTO для представления предмета с информацией о бронированиях и комментариями.
 * Содержит методы для получения последнего и следующего бронирований.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemWithBookingsDto {
    /**
     * Уникальный идентификатор предмета
     */
    private Long id;

    /**
     * Название предмета
     */
    private String name;

    /**
     * Описание предмета
     */
    private String description;

    /**
     * Статус доступности предмета для бронирования
     * true - доступен, false - недоступен
     */
    private Boolean available;

    /**
     * Список всех бронирований предмета
     * Может быть пустым, если бронирований не было
     */
    private List<BookingShortDto> bookings;

    /**
     * Список комментариев к предмету
     * Может быть пустым, если комментариев нет
     */
    private List<CommentDto> comments;

    /**
     * Возвращает последнее завершенное бронирование предмета
     * Бронирование считается последним, если его время окончания самое позднее из всех завершенных
     *
     * @return последнее завершенное бронирование или null, если таких нет
     */
    public BookingShortDto getLastBooking() {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }
        return bookings.stream()
                .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(BookingShortDto::getEnd))
                .orElse(null);
    }

    /**
     * Возвращает следующее предстоящее бронирование предмета
     * Бронирование считается следующим, если его время начала самое раннее из всех будущих
     *
     * @return следующее бронирование или null, если будущих бронирований нет
     */
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
