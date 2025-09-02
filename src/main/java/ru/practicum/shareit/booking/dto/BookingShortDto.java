package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Краткое DTO для отображения информации о бронировании в составе предмета
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingShortDto {
    @NotNull
    private Long id;
    @NotNull
    private Long bookerId;
    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
}
