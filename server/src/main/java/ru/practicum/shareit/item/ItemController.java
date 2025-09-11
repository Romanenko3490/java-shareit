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
 * Обрабатывает HTTP-запросы, связанные с созданием, обновлением, поиском и получением предметов.
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
     * @param userId  ID пользователя-владельца (из заголовка X-Sharer-User-Id)
     * @param request данные для создания предмета
     * @return созданный предмет в формате DTO
     */
    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id")
                              @Min(1) Long userId,
                              @RequestBody
                              @Valid NewItemRequest request) {
        log.info("Создание предмета для пользователя с ID: {}", userId);
        return itemService.addItem(userId, request);
    }

    /**
     * Обновляет существующий предмет.
     *
     * @param userId  ID пользователя-владельца (из заголовка X-Sharer-User-Id)
     * @param itemId  ID обновляемого предмета
     * @param request данные для обновления предмета
     * @return обновленный предмет в формате DTO
     */
    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id")
                              @Min(1) Long userId,
                              @PathVariable
                              @Min(1) Long itemId,
                              @RequestBody
                              @Valid UpdateItemRequest request) {
        log.info("Обновление предмета с ID: {} для пользователя с ID: {}", itemId, userId);
        return itemService.updateItem(userId, itemId, request);
    }

    /**
     * Получает предмет по ID с информацией о бронированиях и комментариях.
     *
     * @param userId ID пользователя, запрашивающего информацию
     * @param itemId ID запрашиваемого предмета
     * @return предмет с информацией о последнем/следующем бронировании и комментариями
     */
    @GetMapping("/{itemId}")
    public ItemLastNextBookingsAndCommentsDto getItem(
            @RequestHeader("X-Sharer-User-Id")
            @Min(1) Long userId,
            @PathVariable
            @Min(1) Long itemId) {
        log.info("Получение предмета с ID: {} пользователем с ID: {}", itemId, userId);
        return itemService.getItem(itemId);
    }

    /**
     * Получает все предметы пользователя с информацией о бронированиях.
     *
     * @param userId ID пользователя-владельца предметов
     * @return коллекция предметов пользователя с информацией о бронированиях
     */
    @GetMapping()
    public Collection<ItemWithBookingsDto> getUserItems(
            @RequestHeader("X-Sharer-User-Id")
            @Min(1) Long userId) {
        log.info("Получение всех предметов пользователя с ID: {}", userId);
        return itemService.getUserItems(userId);
    }

    /**
     * Ищет доступные предметы по тексту в названии или описании.
     *
     * @param text текст для поиска (без учета регистра)
     * @return коллекция найденных доступных предметов
     */
    @GetMapping("/search")
    public Collection<ItemDto> searchItemsByText(@RequestParam("text")
                                                 String text) {
        log.info("Поиск предметов по тексту: {}", text);
        return itemService.searchItemsByText(text);
    }

    /**
     * Добавляет комментарий к предмету.
     *
     * @param userId  ID пользователя, оставляющего комментарий
     * @param itemId  ID предмета, к которому добавляется комментарий
     * @param request данные комментария (текст)
     * @return созданный комментарий в формате DTO
     */
    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id")
                                 @Min(1) Long userId,
                                 @PathVariable @Min(1) Long itemId,
                                 @RequestBody
                                 @Valid NewCommentRequest request) {
        log.info("Добавление комментария к предмету с ID: {} пользователем с ID: {}", itemId, userId);
        return itemService.addComment(userId, itemId, request);
    }
}
