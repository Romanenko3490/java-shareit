package ru.practicum.shareit.request.dto;


import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

/**
 * DTO для запроса на предмет.
 * Используется для передачи данных о запросе между клиентом и сервером.
 */
@NoArgsConstructor
@Data
public class ItemRequestDto {
    private Long id;
    private String description;
    private LocalDateTime created;
}
