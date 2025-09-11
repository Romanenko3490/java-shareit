package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * DTO для представления краткой информации о предмете бронирования.
 * Используется для передачи данных о бронировании между слоями приложения.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemBookingDto {
    /**
     * Уникальный идентификатор предмета.
     * Не может быть null.
     */
    @NotNull
    private Long id;
    /**
     * Название предмета.
     * Не может быть null.
     */
    @NotBlank
    private String name;
}
