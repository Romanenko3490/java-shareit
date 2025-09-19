package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.dto.comments.NewCommentRequest;

import java.util.Collection;
import java.util.Collections;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id")
                              @Min(1) Long userId,
                              @RequestBody
                              @Valid NewItemRequest request) {
        log.info("Создание предмета для пользователя с ID: {}", userId);
        return itemClient.addItem(userId, request);
    }


    @PatchMapping("/{itemId}")
    @CacheEvict(value = "itemsCache", allEntries = true)
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id")
                              @Min(1) Long userId,
                              @PathVariable
                              @Min(1) Long itemId,
                              @RequestBody
                              UpdateItemRequest request) {
        log.info("Обновление предмета с ID: {} для пользователя с ID: {}", itemId, userId);
        return itemClient.updateItem(userId, itemId, request);
    }


    @GetMapping("/{itemId}")
    @Cacheable(value = "itemsCache", key = "{#userId, #itemId}")
    public ItemLastNextBookingsAndCommentsDto getItem(
            @RequestHeader("X-Sharer-User-Id")
            @Min(1) Long userId,
            @PathVariable
            @Min(1) Long itemId) {
        log.info("Получение предмета с ID: {} пользователем с ID: {}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }


    @GetMapping()
    @Cacheable(value = "itemsCache", key = "#userId")
    public Collection<ItemWithBookingsDto> getUserItems(
            @RequestHeader("X-Sharer-User-Id")
            @Min(1) Long userId) {
        log.info("Получение всех предметов пользователя с ID: {}", userId);
        return itemClient.getUserItems(userId);
    }


    @GetMapping("/search")
    @Cacheable(value = "searchCache", key = "#text")
    public Collection<ItemDto> searchItemsByText(@RequestParam("text") String text) {
        log.info("Поиск предметов по тексту: '{}'", text);

        if (text == null || text.isBlank()) {
            log.debug("Текст поиска пустой. Возвращаем пустую коллекцию.");
            return Collections.emptyList();
        }

        return itemClient.searchItemsByText(text);
    }

    @PostMapping("/{itemId}/comment")
    @Cacheable(value = "itemsCache", key = "{#userId, #itemId, #request}")
    public ru.practicum.shareit.item.dto.comments.CommentDto addComment(@RequestHeader("X-Sharer-User-Id")
                                                                        @Min(1) Long userId,
                                                                        @PathVariable
                                                                        @Min(1) Long itemId,
                                                                        @RequestBody
                                                                        @Valid NewCommentRequest request) {
        log.info("Добавление комментария к предмету с ID: {} пользователем с ID: {}", itemId, userId);
        return itemClient.addComment(userId, itemId, request);
    }
}
