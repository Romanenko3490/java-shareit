package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * Модель запроса на предмет.
 * Представляет запрос пользователя на добавление нового предмета.
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
public class ItemRequest {
    /**
     * Идентификатор запроса.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Описание запрашиваемого предмета.
     */
    private String description;

    /**
     * Пользователь, создавший запрос.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
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
     * @param requester   пользователь-инициатор
     */
    public ItemRequest(final Long requestId, final String requestDescription,
                       final User requester) {
        this.id = requestId;
        this.description = requestDescription;
        this.requester = requester;
        this.created = LocalDateTime.now();
    }
}
