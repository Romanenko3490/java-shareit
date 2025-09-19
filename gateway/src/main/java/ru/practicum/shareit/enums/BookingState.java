package ru.practicum.shareit.enums;

import jakarta.validation.ValidationException;


public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;


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
