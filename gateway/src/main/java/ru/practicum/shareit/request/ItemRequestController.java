package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithReplaysDto;
import ru.practicum.shareit.request.dto.NewRequest;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader("X-Sharer-User-Id")
                                     @Min(1) Long userId,
                                     @RequestBody
                                     @Valid NewRequest itemRequest) {
        return itemRequestClient.addRequest(userId, itemRequest);
    }

    @GetMapping
    @Cacheable(value = "itemRequestsCache", key = "#userId")
    public List<ItemRequestWithReplaysDto> getAllRequests(@RequestHeader("X-Sharer-User-Id")
                                                          @Min(1) Long userId) {
        return itemRequestClient.getUserRequestsWithReplays(userId);
    }

    @GetMapping("/all")
    @Cacheable(value = "itemRequestsCache", key = "'allRequests'")
    public List<ItemRequestDto> getAllRequests() {
        return itemRequestClient.getAllRequests();
    }

    @GetMapping("/{requestId}")
    @Cacheable(value = "itemRequestsCache", key = "#requestId")
    public ItemRequestWithReplaysDto getRequest(@PathVariable(value = "requestId")
                                                @Min(1) Long requestId) {
        return itemRequestClient.getRequestById(requestId);
    }

}
