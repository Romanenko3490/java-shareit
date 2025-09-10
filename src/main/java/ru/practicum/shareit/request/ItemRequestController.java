package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithReplaysDto;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

/**
 * Контроллер для работы с запросами на предметы.
 * Обрабатывает HTTP-запросы, связанные с созданием и получением запросов.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestBody NewRequest itemRequest) {
        return itemRequestService.addRequest(userId, itemRequest);
    }

    @GetMapping
    public List<ItemRequestWithReplaysDto> getAllRequests(@RequestHeader("X-Sharer-User-Id")
                                                   Long userId) {
        return itemRequestService.getUserRequestsWithReplays(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto>  getAllRequests() {
        return itemRequestService.getAllRequests();
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithReplaysDto getRequest(@PathVariable(value = "requestId")
                                                    Long requestId) {
        return itemRequestService.getRequestById(requestId);
    }

}
