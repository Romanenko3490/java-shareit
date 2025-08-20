package ru.practicum.shareit.item;


import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.Collection;

public interface ItemService {

    ItemDto addItem(Long userId, NewItemRequest request);

    ItemDto updateItem(Long userId, Long itemId, UpdateItemRequest request);

    ItemDto getItem(Long itemId);

    Collection<ItemDto> getUserItems(Long userId);

}
