package ru.practicum.shareit.enums;

import ru.practicum.shareit.exception.ValidationException;

/**
 * Перечисление допустимых состояний для запроса списка бронирований.
 * Используется как параметр в API.
 */
public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    /**
     * Конвертирует строку в значение Enum, игнорируя регистр.
     *
     * @param state строковое представление состояния
     * @return соответствующее значение BookingState
     * @throws ValidationException если переданное значение не поддерживается
     */
    public static BookingState from(String state) {
        if (state == null || state.isBlank()) {
            return ALL; // или выбросить исключение, в зависимости от логики
        }
        try {
            return BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Преобразуем стандартное исключение в ваше, понятное приложению
            throw new ValidationException("Unknown state: " + state);
        }
    }
}
