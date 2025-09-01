package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.GlobalMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

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
public class ItemService {
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
        User owner = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found"));
        Item item = Item.builder()
                .name(request.getName())
                .description(request.getDescription())
                .owner(owner)
                .available(request.getAvailable())
                .build();
        Item savedItem = itemRepository.save(item);
        log.debug("Creating new item {}", savedItem);
        return GlobalMapper.toDto(savedItem);
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

        if (userId != item.getOwner().getId()) {
            throw new NotFoundException("User with id " + userId + " don't have item with id " + itemId);
        }

        updateFields(item, request);
        Item updatedItem = itemRepository.save(item);
        log.debug("Updated item {}", item);

        return GlobalMapper.toDto(updatedItem);
    }

    /**
     * Получает предмет по ID.
     *
     * @param itemId ID предмета
     * @return найденный предмет
     */
    public ItemDto getItem(Long itemId) {
        log.debug("Getting item {}", itemId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));
        return GlobalMapper.toDto(item);
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

        return itemRepository.findAllByOwner_Id(userId)
                .stream()
                .map(GlobalMapper::toDto)
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
                .map(GlobalMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Вспомогательный метод.
     *
     * @param item    предемет для обновления
     * @param request запрос на обновление
     * @return список найденных предметов
     */
    private Item updateFields(final Item item,
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
