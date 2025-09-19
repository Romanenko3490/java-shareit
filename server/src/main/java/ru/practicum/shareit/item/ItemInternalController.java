package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comments.dto.CommentDto;
import ru.practicum.shareit.item.comments.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.*;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Validated
@Slf4j
public class ItemInternalController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id")
                              Long userId,
                              @RequestBody
                              NewItemRequest request) {
        log.info("Создание предмета для пользователя с ID: {}", userId);
        return itemService.addItem(userId, request);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id")
                              Long userId,
                              @PathVariable
                              Long itemId,
                              @RequestBody
                              UpdateItemRequest request) {
        log.info("Обновление предмета с ID: {} для пользователя с ID: {}", itemId, userId);
        return itemService.updateItem(userId, itemId, request);
    }


    @GetMapping("/{itemId}")
    public ItemLastNextBookingsAndCommentsDto getItem(
            @RequestHeader("X-Sharer-User-Id")
            Long userId,
            @PathVariable
            Long itemId) {
        log.info("Получение предмета с ID: {} пользователем с ID: {}", itemId, userId);
        return itemService.getItem(itemId);
    }


    @GetMapping()
    public Collection<ItemWithBookingsDto> getUserItems(
            @RequestHeader("X-Sharer-User-Id")
            Long userId) {
        log.info("Получение всех предметов пользователя с ID: {}", userId);
        return itemService.getUserItems(userId);
    }


    @GetMapping("/search")
    public Collection<ItemDto> searchItemsByText(@RequestParam("text")
                                                 String text) {
        log.info("Поиск предметов по тексту: {}", text);
        return itemService.searchItemsByText(text);
    }


    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id")
                                 Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody
                                 NewCommentRequest request) {
        log.info("Добавление комментария к предмету с ID: {} пользователем с ID: {}", itemId, userId);
        return itemService.addComment(userId, itemId, request);
    }
}
