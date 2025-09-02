package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.comments.dto.CommentDto;

import java.util.List;

/**
 * DTO для представления предмета с информацией о последнем и следующем бронированиях, а также комментариями.
 * Используется для детального отображения информации о предмете.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemLastNextBookingsAndCommentsDto {
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
     * Информация о последнем бронировании предмета
     * Может быть null, если бронирований не было
     */
    private BookingShortDto lastBooking;

    /**
     * Информация о следующем бронировании предмета
     * Может быть null, если будущих бронирований нет
     */
    private BookingShortDto nextBooking;

    /**
     * Список комментариев к предмету
     * Может быть пустым, если комментариев нет
     */
    private List<CommentDto> comments;
}
