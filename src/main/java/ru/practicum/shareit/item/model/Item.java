package ru.practicum.shareit.item.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
    @Column(nullable = false)
    private Long owner;

    /**
     * ID запроса на предмет.
     */
    private String request;

    /**
     * Конструктор для создания предмета.
     *
     * @param itemId      ID предмета
     * @param itemName    название предмета
     * @param itemDescription описание предмета
     * @param itemAvailable   доступность предмета
     * @param itemOwner   ID владельца
     */
    public Item(final Long itemId,
                final String itemName,
                final String itemDescription,
                final Boolean itemAvailable,
                final Long itemOwner) {
        this.id = itemId;
        this.name = itemName;
        this.description = itemDescription;
        this.available = itemAvailable;
        this.owner = itemOwner;
        this.request = null;
    }
}
