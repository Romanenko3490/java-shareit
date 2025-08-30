package ru.practicum.shareit.exception;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Класс для представления ошибки в ответе API.
 */
@Data
@RequiredArgsConstructor
public class ErrorResponse {
    /**
     * Тип ошибки.
     */
    private String error;

    /**
     * Описание ошибки.
     */
    private String description;
}
