package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для представления предмета.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
     * Владелец предмета
     */
    private Long ownerId;

    /**
     * Доступность предмета для аренды.
     */
    private Boolean available;
}
