package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.Collection;

/**
 * Контроллер для работы с предметами.
 */
@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Validated
public class ItemController {
    /**
     * Сервис для работы с предметами.
     */
    private final ItemServiceImpl itemService;

    /**
     * Создает новый предмет.
     *
     * @param userId  ID пользователя
     * @param request данные для создания предмета
     * @return созданный предмет
     */
    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id")
                              @Min(1) Long userId,
                              @RequestBody
                              @Valid NewItemRequest request) {
        return itemService.addItem(userId, request);
    }

    /**
     * Обновляет существующий предмет.
     *
     * @param userId  ID пользователя
     * @param itemId  ID предмета
     * @param request данные для обновления
     * @return обновленный предмет
     */
    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id")
                              @Min(1) Long userId,
                              @PathVariable
                              @Min(1) Long itemId,
                              @RequestBody
                              @Valid UpdateItemRequest request) {
        return itemService.updateItem(userId, itemId, request);
    }

    /**
     * Получает предмет по ID.
     *
     * @param itemId ID предмета
     * @return найденный предмет
     */
    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable
                           @Min(1) Long itemId) {
        return itemService.getItem(itemId);
    }

    /**
     * Получает все предметы пользователя.
     *
     * @param userId ID пользователя
     * @return коллекция предметов пользователя
     */
    @GetMapping()
    public Collection<ItemDto> getUserItems(
            @RequestHeader("X-Sharer-User-Id")
            @Min(1) Long userId) {
        return itemService.getUserItems(userId);
    }

    /**
     * Ищет предметы по тексту.
     *
     * @param text текст для поиска
     * @return коллекция найденных предметов
     */
    @GetMapping("/search")
    public Collection<ItemDto> searchItemsByText(@RequestParam("text")
                                                 String text) {
        return itemService.searchItemsByText(text);
    }
}
