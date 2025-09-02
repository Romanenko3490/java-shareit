package ru.practicum.shareit.item.comments.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * Сущность комментария к предмету
 * Представляет комментарий, оставленный пользователем к предмету
 */
@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class Comment {
    /**
     * Уникальный идентификатор комментария
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Текст комментария
     * Не может быть пустым, максимальная длина - 2000 символов
     */
    @NotBlank
    @Column(nullable = false, length = 2000)
    private String text;

    /**
     * Предмет, к которому относится комментарий
     * Связь многие-к-одному с сущностью Item
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    /**
     * Автор комментария
     * Связь многие-к-одному с сущностью User
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    /**
     * Дата и время создания комментария
     * Автоматически устанавливается при создании комментария
     */
    private LocalDateTime created;

    /**
     * Конструктор для создания нового комментария
     * Автоматически устанавливает текущее время создания
     *
     * @param text   текст комментария
     * @param item   предмет, к которому относится комментарий
     * @param author автор комментария
     */
    @Builder
    public Comment(String text, Item item, User author) {
        this.text = text;
        this.item = item;
        this.author = author;
        this.created = LocalDateTime.now();
    }
}
