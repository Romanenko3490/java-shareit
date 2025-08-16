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
    private static long ITEM_ID_COUNTER = 0;
    private static long USER_ID_COUNTER = 0;

    private Map<Long, List<Item>> usersItems = new HashMap<>();
    private Map<Long, User> users = new HashMap<>();


    //Items
    public static void increaseItemId() {
        ITEM_ID_COUNTER++;
    }

    public static long getItemId() {
        return ITEM_ID_COUNTER;
    }

    public static void dropItemIdCounter() {
        ITEM_ID_COUNTER = 0;
    }

    //Users
    public static long getUserId() {
        return USER_ID_COUNTER;
    }

    public static void increaseUserId() {
        USER_ID_COUNTER++;
    }

    public static long dropUserId() {
        return USER_ID_COUNTER = 0;
    }
}
