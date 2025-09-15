package ru.practicum.shareit.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GetawayExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ViolationErrorResponse handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {

        List<Violation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        log.error("Validation failed: {}", violations);
        return new ViolationErrorResponse(violations);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ViolationErrorResponse handleConstraintViolationException(
            ConstraintViolationException ex) {

        List<Violation> violations = ex.getConstraintViolations().stream()
                .map(violation -> new Violation(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()))
                .collect(Collectors.toList());

        log.error("Constraint violation: {}", violations);
        return new ViolationErrorResponse(violations);
    }

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

    @ExceptionHandler(WebClientResponseException.NotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ViolationErrorResponse handleNotFoundException(WebClientResponseException.NotFound e) {
        log.error("Not Found: {}", e.getMessage());
        return new ViolationErrorResponse(List.of(new Violation("Not Found", e.getMessage())));
    }
}