package ru.practicum.shareit.item.comments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO для представления комментария к предмету
 * Используется для передачи данных о комментарии между клиентом и сервером
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    /**
     * Уникальный идентификатор комментария
     */
    @NotNull
    private Long id;

    /**
     * Текст комментария
     * Не может быть пустым и не должен превышать 1000 символов
     */
    @NotBlank
    @Size(max = 1000, message = "Comment cannot be longer than 1000 characters")
    private String text;

    /**
     * Имя автора комментария
     * Не может быть пустым
     */
    @NotBlank
    private String authorName;

    /**
     * Дата и время создания комментария
     * Не может быть null
     */
    @NotNull
    private LocalDateTime created;
}
