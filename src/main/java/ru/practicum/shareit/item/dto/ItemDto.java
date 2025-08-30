package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для представления предмета.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
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
}
