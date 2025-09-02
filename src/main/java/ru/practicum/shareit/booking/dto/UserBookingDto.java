package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * DTO для представления краткой информации о пользователе.
 * Используется для передачи данных о бронировании между слоями приложения.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBookingDto {
    /**
     * Уникальный идентификатор пользователя.
     * Не может быть null.
     */
    @NotNull
    private Long id;
    /**
     * Имя пользователя.
     * Не может быть null.
     */
    @NotBlank
    private String name;
}
