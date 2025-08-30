package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для работы с предметами.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl {
    /**
     * Репозитории.
     */
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    /**
     * Добавляет новый предмет.
     *
     * @param userId  ID пользователя
     * @param request данные предмета
     * @return созданный предмет
     */
    public ItemDto addItem(Long userId, NewItemRequest request) {
        log.debug("Adding new item");
        if (!userRepository.findById(userId).isPresent()) {
            throw new NotFoundException("User not found");
        }
        Item item = ItemMapper.mapToItem(userId, request);
        log.debug("Creating new item {}", item);
        return ItemMapper.mapToItemDto(itemRepository.save(item));
    }

    /**
     * Обновляет существующий предмет.
     *
     * @param userId  ID пользователя
     * @param itemId  ID предмета
     * @param request данные для обновления
     * @return обновленный предмет
     */
    public ItemDto updateItem(Long userId, Long itemId,
                              UpdateItemRequest request) {
        log.debug("Updating itemId {}", itemId);
        log.debug("Request {}", request);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (userId != item.getOwnerId()) {
            throw new NotFoundException("User with id " + userId + " don't have item with id " + itemId);
        }

        Item updatedItem = ItemMapper.updateItem(item, request);
        log.debug("Updated item {}", updatedItem);

        return ItemMapper.mapToItemDto(updatedItem);
    }

    /**
     * Получает предмет по ID.
     *
     * @param itemId ID предмета
     * @return найденный предмет
     */
    public ItemDto getItem(Long itemId) {
        log.debug("Getting item {}", itemId);
        return ItemMapper.mapToItemDto(itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found")));
    }

    /**
     * Получает все предметы пользователя.
     *
     * @param userId ID пользователя
     * @return коллекция предметов пользователя
     */
    public Collection<ItemDto> getUserItems(Long userId) {
        log.debug("Getting items by user {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }

        return itemRepository.findAllByOwnerId(userId)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    /**
     * Ищет предметы по тексту.
     *
     * @param text текст для поиска
     * @return список найденных предметов
     */
    public List<ItemDto> searchItemsByText(String text) {
        log.debug("Searching items by text {}", text);
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        String searchText = text.toLowerCase();
        log.debug("Searching items by text {}", searchText);
        return itemRepository.searchAvailableItemsByText(searchText).stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

}
