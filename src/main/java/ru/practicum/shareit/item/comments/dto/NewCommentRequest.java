package ru.practicum.shareit.item.comments.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для создания нового комментария
 * Используется для получения данных от клиента при создании комментария
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCommentRequest {
    /**
     * Текст комментария
     * Содержит содержание комментария, которое пользователь хочет добавить к предмету
     */
    private String text;
}
