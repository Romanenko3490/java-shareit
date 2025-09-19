package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;


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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;


    @Column(nullable = false)
    private String name;


    private String description;


    @Column(nullable = false)
    private Boolean available;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private ItemRequest request;


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
