package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comments.dto.CommentDto;
import ru.practicum.shareit.item.comments.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.*;

import java.util.Collection;

/**
 * Контроллер для работы с предметами.
 */
@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Validated
@Slf4j
public class ItemController {
    /**
     * Сервис для работы с предметами.
     */
    private final ItemService itemService;

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
    public ItemLastNextBookingsAndCommentsDto getItem(
            @RequestHeader("X-Sharer-User-Id")
            @Min(1) Long userId,
            @PathVariable
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
    public Collection<ItemWithBookingsDto> getUserItems(
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

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id")
                                 @Min(1) Long userId,
                                 @PathVariable @Min(1) Long itemId,
                                 @RequestBody
                                 @Valid NewCommentRequest request) {
        return itemService.addComment(userId, itemId, request);
    }
}
