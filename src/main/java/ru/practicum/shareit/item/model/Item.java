package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * Сущность предмета.
 */
@Entity
@Table(name = "items")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Builder
public class Item {
    /**
     * Идентификатор предмета.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Название предмета.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Описание предмета.
     */
    private String description;

    /**
     * Доступность предмета для аренды.
     */
    @Column(nullable = false)
    private Boolean available;

    /**
     * Идентификатор владельца предмета.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    /**
     * ID запроса на предмет.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private ItemRequest request;

    /**
     * Конструктор для создания предмета.
     *
     * @param itemId          ID предмета
     * @param itemName        название предмета
     * @param itemDescription описание предмета
     * @param itemAvailable   доступность предмета
     * @param owner           владелец
     */
    public Item(final Long itemId,
                final String itemName,
                final String itemDescription,
                final Boolean itemAvailable,
                final User owner) {
        this.id = itemId;
        this.name = itemName;
        this.description = itemDescription;
        this.available = itemAvailable;
        this.owner = owner;
    }
}
