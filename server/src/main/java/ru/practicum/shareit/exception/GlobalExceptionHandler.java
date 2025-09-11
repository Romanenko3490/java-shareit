package ru.practicum.shareit.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Глобальный обработчик исключений для всего приложения.
 * Обрабатывает различные типы исключений и возвращает
 * соответствующие HTTP-статусы.
 */
@Slf4j
@ControllerAdvice
public final class GlobalExceptionHandler {

    /**
     * Обрабатывает исключения нарушения ограничений валидации.
     *
     * @param e исключение ConstraintViolationException
     * @return ответ с нарушениями валидации
     */
    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ViolationErrorResponse handleConstraintViolationException(
            final ConstraintViolationException e) {
        final List<Violation> violations = e.getConstraintViolations().stream()
                .map(cv -> new Violation(
                        cv.getPropertyPath().toString(),
                        cv.getMessage()))
                .collect(Collectors.toList());
        log.error("ConstraintViolationException ({}) : {}",
                violations.size(), violations);
        return new ViolationErrorResponse(violations);
    }

    /**
     * Обрабатывает исключения невалидных аргументов метода.
     *
     * @param e исключение MethodArgumentNotValidException
     * @return ответ с нарушениями валидации
     */
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ViolationErrorResponse handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException e) {
        final List<Violation> violations = e.getBindingResult()
                .getFieldErrors().stream()
                .map(violation -> new Violation(
                        violation.getField(),
                        violation.getDefaultMessage()
                ))
                .collect(Collectors.toList());
        log.error("Method Argument Not Valid ({}) : {}",
                violations.size(), violations);
        return new ViolationErrorResponse(violations);
    }

    /**
     * Обрабатывает пользовательские исключения валидации.
     *
     * @param e исключение ValidationException
     * @return ответ с нарушениями валидации
     */
    @ResponseBody
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ViolationErrorResponse handleValidationException(
            final ValidationException e) {
        log.error("Validation Exception ({}) : {}",
                e.getClass().getName(), e.getMessage());
        return new ViolationErrorResponse(List.of(new Violation(
                "Validation Failed", e.getMessage())));
    }

    /**
     * Обрабатывает исключения "не найдено".
     *
     * @param e исключение NotFoundException
     * @return ответ с нарушением
     */
    @ResponseBody
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ViolationErrorResponse handleNotFoundException(
            final NotFoundException e) {
        log.error("Not Found Exception ({}) : {}",
                e.getClass().getName(), e.getMessage());
        return new ViolationErrorResponse(List.of(new Violation(
                "Not Found", e.getMessage())));
    }

    /**
     * Обрабатывает общие исключения.
     *
     * @param e исключение Exception
     * @return ответ с ошибкой сервера
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ViolationErrorResponse handleException(
            final Exception e) {
        log.error("Internal Server Error ({}) : {}",
                e.getClass().getName(), e.getMessage());
        return new ViolationErrorResponse(List.of(new Violation(
                "Internal Server Error", e.getMessage())));
    }

    /**
     * Обрабатывает исключения валидации Jakarta.
     *
     * @param e исключение jakarta.validation.ValidationException
     * @return ответ с нарушениями валидации
     */
    @ResponseBody
    @ExceptionHandler(jakarta.validation.ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ViolationErrorResponse handleJakartaValidationException(
            final jakarta.validation.ValidationException e) {
        log.error("Validation Exception ({}) : {}",
                e.getClass().getName(), e.getMessage());
        return new ViolationErrorResponse(List.of(new Violation(
                "Validation Failed", e.getMessage())));
    }

    /**
     * Обрабатывает исключения дублирования ключей.
     *
     * @param e исключение DuplicateKeyException
     * @return ответ с конфликтом
     */
    @ResponseBody
    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ViolationErrorResponse handleDuplicateKeyException(
            final DuplicateKeyException e) {
        log.error("Duplicate Key ({}) : {}",
                e.getClass().getName(), e.getMessage());
        return new ViolationErrorResponse(List.of(new Violation(
                "Duplicate Key", e.getMessage())));
    }
}
