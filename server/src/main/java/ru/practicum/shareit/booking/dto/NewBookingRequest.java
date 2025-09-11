package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO для создания нового запроса на бронирование.
 * Используется для получения данных от клиента при создании бронирования.
 */
@Data
public class NewBookingRequest {
    /**
     * Уникальный идентификатор предмета, который требуется забронировать.
     * Не может быть null.
     */
    @NotNull
    private Long itemId;

    /**
     * Дата и время начала бронирования.
     * Должна быть указана (не пустая) и быть в будущем времени.
     */
    @NotNull
    @Future
    private LocalDateTime start;

    /**
     * Дата и время окончания бронирования.
     * Должна быть в будущем времени.
     */
    @NotNull
    @Future
    private LocalDateTime end;
}
