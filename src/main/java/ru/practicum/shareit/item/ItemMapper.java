package ru.practicum.shareit.item;


import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;

@Slf4j
@UtilityClass
public class ItemMapper {
    public static Item mapToItem(Long userId, NewItemRequest request) {
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

    public static ItemDto mapToItemDto(Item item) {
        log.info("Mapping item to ItemDto");
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        return itemDto;
    }

    public static Item updateItem(Item item, UpdateItemRequest request) {
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
