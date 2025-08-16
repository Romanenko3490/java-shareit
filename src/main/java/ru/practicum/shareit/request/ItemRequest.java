package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemRequest {
    private Long id;

    private String description;

    private User requester;
    private LocalDateTime created;


    public ItemRequest(Long id, String description, User requester) {
        this.id = id;
        this.description = description;
        this.requester = requester;
        this.created = LocalDateTime.now();
    }
}