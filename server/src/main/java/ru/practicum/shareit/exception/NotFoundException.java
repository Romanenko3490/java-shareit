package ru.practicum.shareit.exception;

/**
 * Исключение, выбрасываемое когда объект не найден.
 */
public class NotFoundException extends RuntimeException {
    /**
     * Конструктор с сообщением об ошибке.
     *
     * @param message сообщение об ошибке
     */
    public NotFoundException(final String message) {
        super(message);
    }
}
