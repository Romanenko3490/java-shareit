package ru.practicum.shareit.storage;

import lombok.Getter;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Getter
public class InMemoryStorage {
    private Map<Long, List<Item>> usersItems = new HashMap<>();
    private Map<Long, User> users = new HashMap<>();
}
