package ru.practicum.shareit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Класс, представляющий нарушение валидации.
 */
@Getter
@RequiredArgsConstructor
public class Violation {
    /**
     * Поле, в котором произошло нарушение.
     */
    private final String field;

    /**
     * Сообщение о нарушении.
     */
    private final String message;
}
