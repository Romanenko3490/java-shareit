package ru.practicum.shareit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Getter
@RequiredArgsConstructor
public class Violation {
    private final String field;
    private final String message;

}
