package ru.practicum.shareit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Класс-обертка для ответа с нарушениями валидации.
 */
@Getter
@RequiredArgsConstructor
public class ViolationErrorResponse {
    /**
     * Список нарушений валидации.
     */
    private final List<Violation> violations;
}
