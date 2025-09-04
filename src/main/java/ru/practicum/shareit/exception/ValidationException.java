package ru.practicum.shareit.exception;

/**
 * Исключение, выбрасываемое при ошибках валидации.
 */
public class ValidationException extends RuntimeException {
    /**
     * Конструктор с сообщением об ошибке.
     *
     * @param message сообщение об ошибке
     */
    public ValidationException(final String message) {
        super(message);
    }
}
