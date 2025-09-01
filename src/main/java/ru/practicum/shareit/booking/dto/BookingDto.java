package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

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
    @NotNull
    private Long id;

    /**
     * Дата и время начала бронирования.
     * Не может быть null.
     */
    @NotNull
    private LocalDateTime start;

    /**
     * Дата и время окончания бронирования.
     * Не может быть null.
     */
    @NotNull
    private LocalDateTime end;

    /**
     * Предмет, который был забронирован.
     * Не может быть null.
     */
    @NotNull
    private ItemDto item;

    /**
     * Пользователь, который осуществил бронирование.
     * Не может быть null.
     */
    @NotNull
    private UserDto booker;

    /**
     * Статус бронирования.
     * Может содержать значения: APPROVED, REJECTED, WAITING, CANCELED и т.д.
     */
    private String status;
}
