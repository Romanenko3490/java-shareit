package ru.practicum.shareit.storage;

import lombok.Getter;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory хранилище для данных приложения.
 * Хранит пользователей и предметы в памяти.
 */
@Repository
@Getter
public class InMemoryStorage {
    /**
     * Счетчик для генерации ID предметов.
     */
    private static long itemIdCounter = 0;

    /**
     * Счетчик для генерации ID пользователей.
     */
    private static long userIdCounter = 0;

    /**
     * Карта предметов по пользователям.
     */
    private Map<Long, List<Item>> usersItems = new HashMap<>();

    /**
     * Карта пользователей.
     */
    private Map<Long, User> users = new HashMap<>();

    /**
     * Увеличивает счетчик ID предметов.
     */
    public static void increaseItemId() {
        itemIdCounter++;
    }

    /**
     * Возвращает текущее значение счетчика ID предметов.
     *
     * @return текущий ID предмета
     */
    public static long getItemId() {
        return itemIdCounter;
    }

    /**
     * Сбрасывает счетчик ID предметов.
     */
    public static void dropItemIdCounter() {
        itemIdCounter = 0;
    }

    /**
     * Возвращает текущее значение счетчика ID пользователей.
     *
     * @return текущий ID пользователя
     */
    public static long getUserId() {
        return userIdCounter;
    }

    /**
     * Увеличивает счетчик ID пользователей.
     */
    public static void increaseUserId() {
        userIdCounter++;
    }

    /**
     * Сбрасывает счетчик ID пользователей.
     *
     * @return новое значение счетчика (0)
     */
    public static long dropUserId() {
        userIdCounter = 0;
        return userIdCounter;
    }
}
