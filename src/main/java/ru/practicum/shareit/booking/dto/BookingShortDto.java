package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Краткое DTO для отображения информации о бронировании в составе предмета
 * Содержит только базовые поля, необходимые для отображения в контексте предмета
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingShortDto {
    /**
     * Уникальный идентификатор бронирования
     */
    @NotNull
    private Long id;

    /**
     * Идентификатор пользователя, который осуществил бронирование
     */
    @NotNull
    private Long bookerId;

    /**
     * Дата и время начала бронирования
     * Не может быть null
     */
    @NotNull
    private LocalDateTime start;

    /**
     * Дата и время окончания бронирования
     * Не может быть null
     */
    @NotNull
    private LocalDateTime end;
}
