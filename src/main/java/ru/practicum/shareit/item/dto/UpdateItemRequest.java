package ru.practicum.shareit.item.dto;

import lombok.Data;

/**
 * DTO для обновления предмета.
 */
@Data
public final class UpdateItemRequest {
    /**
     * Название предмета.
     */
    private String name;

    /**
     * Описание предмета.
     */
    private String description;

    /**
     * Доступность предмета.
     */
    private Boolean available;

    /**
     * Проверяет, содержит ли запрос имя.
     *
     * @return true если имя присутствует и не пустое
     */
    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    /**
     * Проверяет, содержит ли запрос описание.
     *
     * @return true если описание присутствует и не пустое
     */
    public boolean hasDescription() {
        return !(description == null || description.isBlank());
    }

    /**
     * Проверяет, содержит ли запрос статус доступности.
     *
     * @return true если статус доступности присутствует
     */
    public boolean hasAvailable() {
        return available != null;
    }
}
