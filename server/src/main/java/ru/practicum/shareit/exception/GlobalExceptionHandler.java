package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public final class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(
            final ValidationException e) {
        log.error("Validation Exception ({}) : {}",
                e.getClass().getName(), e.getMessage());
        return new ErrorResponse(e.getClass().getName(), e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(
            final NotFoundException e) {
        log.error("Not Found Exception ({}) : {}",
                e.getClass().getName(), e.getMessage());
        return new ErrorResponse(e.getClass().getName(), e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(
            final Exception e) {
        log.error("Internal Server Error ({}) : {}",
                e.getClass().getName(), e.getMessage());
        return new ErrorResponse(e.getClass().getName(), e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateKeyException(
            final DuplicateKeyException e) {
        log.error("Duplicate Key ({}) : {}",
                e.getClass().getName(), e.getMessage());
        return new ErrorResponse(e.getClass().getName(), e.getMessage());
    }
}
