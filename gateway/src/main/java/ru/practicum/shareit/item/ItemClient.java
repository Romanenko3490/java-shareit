package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.base.BaseWebClient;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.dto.comments.CommentDto;
import ru.practicum.shareit.item.dto.comments.NewCommentRequest;

import java.util.Collection;

@Service
public class ItemClient extends BaseWebClient {
    private static final String API_PREFIX = "/items";
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl) {
        super(serverUrl, API_PREFIX);
    }


    public ItemDto addItem(Long userId, NewItemRequest request) {
        return webClient.post()
                .header(USER_ID_HEADER, userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ItemDto.class)
                .block();
    }

    public ItemDto updateItem(Long userId, Long itemId, UpdateItemRequest request) {
        return webClient.patch()
                .uri("/" + itemId)
                .header(USER_ID_HEADER, userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ItemDto.class)
                .block();
    }

    public ItemLastNextBookingsAndCommentsDto getItem(Long userId, Long itemId) {
        return webClient.get()
                .uri("/" + itemId)
                .header(USER_ID_HEADER, userId.toString())
                .retrieve()
                .bodyToMono(ItemLastNextBookingsAndCommentsDto.class)
                .block();
    }

    public Collection<ItemWithBookingsDto> getUserItems(Long userId) {
        return webClient.get()
                .header(USER_ID_HEADER, userId.toString())
                .retrieve()
                .bodyToFlux(ItemWithBookingsDto.class)
                .collectList()
                .block();
    }

    public Collection<ItemDto> searchItemsByText(String text) {
        return webClient.get()
                .uri("search?text=" + text)
                .retrieve()
                .bodyToFlux(ItemDto.class)
                .collectList()
                .block();
    }

    public CommentDto addComment(Long userId, Long itemId, NewCommentRequest request) {
        return webClient.post()
                .uri("/" + itemId + "/comment")
                .header(USER_ID_HEADER, userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(CommentDto.class)
                .block();
    }
}
