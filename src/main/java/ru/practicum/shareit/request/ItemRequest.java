package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * Модель запроса на предмет.
 * Представляет запрос пользователя на добавление нового предмета.
 */
@Data
@AllArgsConstructor
public class ItemRequest {
    /**
     * Идентификатор запроса.
     */
    private Long id;

    /**
     * Описание запрашиваемого предмета.
     */
    private String description;

    /**
     * Пользователь, создавший запрос.
     */
    private User requester;

    /**
     * Дата и время создания запроса.
     */
    private LocalDateTime created;

    /**
     * Конструктор для создания запроса.
     *
     * @param requestId          идентификатор запроса
     * @param requestDescription описание запроса
     * @param requestRequester   пользователь-инициатор
     */
    public ItemRequest(final Long requestId, final String requestDescription,
                       final User requestRequester) {
        this.id = requestId;
        this.description = requestDescription;
        this.requester = requestRequester;
        this.created = LocalDateTime.now();
    }
}
