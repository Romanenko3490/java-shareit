package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.storage.InMemoryStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemInMemoryServiceImpl implements ItemService {
    private final InMemoryStorage storage;

    @Override
    public ItemDto addItem(Long userId, NewItemRequest request) {
        Item item = ItemMapper.mapToItem(userId, request);
        if (storage.getUsersItems().containsKey(userId)) {
            InMemoryStorage.increaseItemId();
            item.setId(InMemoryStorage.getItemId());
            storage.getUsersItems().get(userId).add(item);
        } else {
            throw new NotFoundException("User not found");
        }
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, UpdateItemRequest request) {
        if (!storage.getUsersItems().containsKey(userId)) {
            throw new NotFoundException("You are using userId which is not exists");
        }

        List<Item> existsItems = storage.getUsersItems().get(userId);

        Item existsItem = existsItems.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not exists"));

        Item udatedItem = ItemMapper.updateItem(existsItem, request);
        existsItems.remove(existsItem);
        existsItems.add(udatedItem);
        return ItemMapper.mapToItemDto(existsItem);
    }


    @Override
    public ItemDto getItem(Long itemId) {

        return storage.getUsersItems().values().stream()
                .flatMap(List::stream)
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .map(ItemMapper::mapToItemDto)
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found"));
    }

    @Override
    public Collection<ItemDto> getUserItems(Long userId) {

        if (!storage.getUsersItems().containsKey(userId)) {
            throw new NotFoundException("You are using userId which is not exists");
        }

        return storage.getUsersItems().get(userId).stream()
                .map(ItemMapper::mapToItemDto).collect(Collectors.toList());
    }

    public List<ItemDto> searchItemsByText(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        String searchText = text.toLowerCase();

        return storage.getUsersItems().values().stream()
                .flatMap(List::stream)
                .filter(item -> item.getAvailable())
                .filter(item -> containsText(item, searchText))
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }


    private boolean containsText(Item item, String searchText) {
        return item.getDescription().toLowerCase().contains(searchText) ||
                item.getName().toLowerCase().contains(searchText);
    }
}
