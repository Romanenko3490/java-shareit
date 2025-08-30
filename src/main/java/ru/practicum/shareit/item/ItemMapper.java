package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;

/**
 * Утилитарный класс для маппинга между DTO и сущностями предметов.
 */
@Slf4j
public class ItemMapper {

    /**
     * Приватный конструктор для утилитного класса.
     */
    private ItemMapper() {
        // Утилитный класс
    }

    /**
     * Преобразует запрос в сущность предмета.
     *
     * @param userId  ID пользователя
     * @param request запрос на создание предмета
     * @return сущность предмета
     */
    public static Item mapToItem(final Long userId,
                                 final NewItemRequest request) {
        log.info("Mapping request to Item");
        Item item = new Item();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setOwner(userId);
        item.setAvailable(request.getAvailable());

        if (request.getRequest() != null) {
            item.setRequest(request.getRequest());
        }
        return item;
    }

    /**
     * Преобразует сущность предмета в DTO.
     *
     * @param item сущность предмета
     * @return DTO предмета
     */
    public static ItemDto mapToItemDto(final Item item) {
        log.info("Mapping item to ItemDto");
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        return itemDto;
    }

    /**
     * Обновляет сущность предмета данными из запроса.
     *
     * @param item    сущность предмета
     * @param request запрос на обновление
     * @return обновленная сущность предмета
     */
    public static Item updateItem(final Item item,
                                  final UpdateItemRequest request) {
        if (request.hasName()) {
            item.setName(request.getName());
        }
        if (request.hasDescription()) {
            item.setDescription(request.getDescription());
        }
        if (request.hasAvailable()) {
            item.setAvailable(request.getAvailable());
        }
        return item;
    }
}
