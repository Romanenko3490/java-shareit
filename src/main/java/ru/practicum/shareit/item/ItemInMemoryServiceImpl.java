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

/**
 * Реализация сервиса для работы с предметами.
 */
@Service
@RequiredArgsConstructor
public final class ItemInMemoryServiceImpl implements ItemService {
    /**
     * Хранилище данных.
     */
    private final InMemoryStorage storage;

    /**
     * Добавляет новый предмет.
     *
     * @param userId  ID пользователя
     * @param request данные предмета
     * @return созданный предмет
     */
    @Override
    public ItemDto addItem(final Long userId, final NewItemRequest request) {
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

    /**
     * Обновляет существующий предмет.
     *
     * @param userId  ID пользователя
     * @param itemId  ID предмета
     * @param request данные для обновления
     * @return обновленный предмет
     */
    @Override
    public ItemDto updateItem(final Long userId, final Long itemId,
                              final UpdateItemRequest request) {
        if (!storage.getUsersItems().containsKey(userId)) {
            throw new NotFoundException(
                    "You are using userId which is not exists");
        }

        List<Item> existsItems = storage.getUsersItems().get(userId);

        Item existsItem = existsItems.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(
                        "Item with id " + itemId + " not exists"));

        Item udatedItem = ItemMapper.updateItem(existsItem, request);
        existsItems.remove(existsItem);
        existsItems.add(udatedItem);
        return ItemMapper.mapToItemDto(existsItem);
    }

    /**
     * Получает предмет по ID.
     *
     * @param itemId ID предмета
     * @return найденный предмет
     */
    @Override
    public ItemDto getItem(final Long itemId) {
        return storage.getUsersItems().values().stream()
                .flatMap(List::stream)
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .map(ItemMapper::mapToItemDto)
                .orElseThrow(() -> new NotFoundException("Item with id "
                        + itemId + " not found"));
    }

    /**
     * Получает все предметы пользователя.
     *
     * @param userId ID пользователя
     * @return коллекция предметов пользователя
     */
    @Override
    public Collection<ItemDto> getUserItems(final Long userId) {
        if (!storage.getUsersItems().containsKey(userId)) {
            throw new NotFoundException(
                    "You are using userId which is not exists");
        }

        return storage.getUsersItems().get(userId).stream()
                .map(ItemMapper::mapToItemDto).collect(Collectors.toList());
    }

    /**
     * Ищет предметы по тексту.
     *
     * @param text текст для поиска
     * @return список найденных предметов
     */
    public List<ItemDto> searchItemsByText(final String text) {
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

    private boolean containsText(final Item item, final String searchText) {
        return item.getDescription().toLowerCase().contains(searchText)
                || item.getName().toLowerCase().contains(searchText);
    }
}
