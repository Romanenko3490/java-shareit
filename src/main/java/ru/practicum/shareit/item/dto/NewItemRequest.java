package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jdk.jfr.BooleanFlag;
import lombok.Data;

/**
 * DTO для создания нового предмета.
 */
@Data
public class NewItemRequest {
    /**
     * Название предмета.
     */
    @NotBlank(message = "Name cannot be Empty")
    private String name;

    /**
     * Описание предмета.
     */
    @NotBlank(message = "Description cannot be Empty")
    private String description;

    /**
     * Доступность предмета.
     */
    @BooleanFlag
    @NotNull
    private Boolean available;

    /**
     * ID запроса на предмет.
     */
    private Long requestId;
}
