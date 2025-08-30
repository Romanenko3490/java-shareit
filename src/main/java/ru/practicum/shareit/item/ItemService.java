package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.Collection;

/**
 * Интерфейс сервиса для работы с предметами.
 */
public interface ItemService {
    /**
     * Добавляет новый предмет.
     *
     * @param userId  ID пользователя
     * @param request данные предмета
     * @return созданный предмет
     */
    ItemDto addItem(Long userId, NewItemRequest request);

    /**
     * Обновляет существующий предмет.
     *
     * @param userId  ID пользователя
     * @param itemId  ID предмета
     * @param request данные для обновления
     * @return обновленный предмет
     */
    ItemDto updateItem(Long userId, Long itemId, UpdateItemRequest request);

    /**
     * Получает предмет по ID.
     *
     * @param itemId ID предмета
     * @return найденный предмет
     */
    ItemDto getItem(Long itemId);

    /**
     * Получает все предметы пользователя.
     *
     * @param userId ID пользователя
     * @return коллекция предметов пользователя
     */
    Collection<ItemDto> getUserItems(Long userId);
}
