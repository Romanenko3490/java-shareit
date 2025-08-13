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

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Validated
public class ItemController {
    private final ItemInMemoryServiceImpl itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                              @RequestBody @Valid NewItemRequest request) {
        return itemService.addItem(userId, request);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                              @PathVariable @Min(1) Long itemId,
                              @RequestBody @Valid UpdateItemRequest request) {
        return itemService.updateItem(userId, itemId, request);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable @Min(1) Long itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping()
    public Collection<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId) {
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItemsByText(@RequestParam("text") String text) {
        return itemService.searchItemsByText(text);
    }


}
