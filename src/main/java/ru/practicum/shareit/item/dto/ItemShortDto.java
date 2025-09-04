package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для краткого представления предмета.
 * Используется для отображения базовой информации о предмете без деталей.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemShortDto {
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
     * Статус активности предмета
     * true - предмет активен и доступен для операций
     * false - предмет неактивен
     */
    private Boolean isActive;
}
