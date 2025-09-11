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
@Table(name = "requests")
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
    @Column(name = "created", insertable = false, updatable = false)
    private LocalDateTime created;

    public ItemRequest(String description, User requester) {
        this.description = description;
        this.requester = requester;
    }
}
