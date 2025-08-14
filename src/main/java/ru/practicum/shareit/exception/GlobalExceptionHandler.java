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

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ViolationErrorResponse handleConstraintViolationException(ConstraintViolationException e) {
        final List<Violation> violations = e.getConstraintViolations().stream()
                .map(cv -> new Violation(
                        cv.getPropertyPath().toString(),
                        cv.getMessage()))
                .collect(Collectors.toList());
        return new ViolationErrorResponse(violations);
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ViolationErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final List<Violation> violations = e.getBindingResult().getFieldErrors().stream()
                .map(violation -> new Violation(
                        violation.getField().toString(),
                        violation.getDefaultMessage()
                ))
                .collect(Collectors.toList());
        log.error("Method Argument Not Valid ({}) : {}", violations.size(), violations);
        return new ViolationErrorResponse(violations);
    }

    @ResponseBody
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ViolationErrorResponse handleValidationException(ValidationException e) {
        log.error("Validation Exception ({}) : {}", e.getClass().getName(), e.getMessage());
        return new ViolationErrorResponse(List.of(new Violation("Validation Failed", e.getMessage())));
    }

    @ResponseBody
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ViolationErrorResponse handleNotFoundException(NotFoundException e) {
        log.error("Not Found Exception ({}) : {}", e.getClass().getName(), e.getMessage());
        return new ViolationErrorResponse(List.of(new Violation("Not Found", e.getMessage())));
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ViolationErrorResponse handleException(Exception e) {
        log.error("Internal Server Error ({}) : {}", e.getClass().getName(), e.getMessage());
        return new ViolationErrorResponse(List.of(new Violation("Internal Server Error", e.getMessage())));
    }

    @ResponseBody
    @ExceptionHandler(jakarta.validation.ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ViolationErrorResponse handleValidationException(jakarta.validation.ValidationException e) {
        log.error("Validation Exception ({}) : {}", e.getClass().getName(), e.getMessage());
        return new ViolationErrorResponse(List.of(new Violation("Validation Failed", e.getMessage())));
    }

    @ResponseBody
    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ViolationErrorResponse handleDuplicateKeyException(DuplicateKeyException e) {
        log.error("Duplicate Key ({}) : {}", e.getClass().getName(), e.getMessage());
        return new ViolationErrorResponse(List.of(new Violation("Duplicate Key", e.getMessage())));
    }


}
