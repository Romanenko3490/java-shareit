package ru.practicum.shareit.booking.dto;


import jakarta.validation.constraints.Future;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDateTime;


@Data
public class NewBookingRequest {

    @NotNull
    private Long itemId;

    @NotNull
    private LocalDateTime start;

    @NotNull
    @Future
    private LocalDateTime end;
}
