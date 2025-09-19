package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.base.BaseWebClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithReplaysDto;
import ru.practicum.shareit.request.dto.NewRequest;

import java.util.List;

@Service
public class ItemRequestClient extends BaseWebClient {
    private static final String API_PREFIX = "/requests";
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl) {
        super(serverUrl, API_PREFIX);
    }

    public ItemRequestDto addRequest(Long userId, NewRequest itemRequest) {
        return webClient.post()
                .header(USER_ID_HEADER, userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(itemRequest)
                .retrieve()
                .bodyToMono(ItemRequestDto.class)
                .block();
    }

    public List<ItemRequestWithReplaysDto> getUserRequestsWithReplays(Long userId) {
        return webClient.get()
                .header(USER_ID_HEADER, userId.toString())
                .retrieve()
                .bodyToFlux(ItemRequestWithReplaysDto.class)
                .collectList()
                .block();
    }

    public List<ItemRequestDto> getAllRequests() {
        return webClient.get()
                .uri("/all")
                .retrieve()
                .bodyToFlux(ItemRequestDto.class)
                .collectList()
                .block();
    }

    public ItemRequestWithReplaysDto getRequestById(Long requestId) {
        return webClient.get()
                .uri("/" + requestId)
                .retrieve()
                .bodyToMono(ItemRequestWithReplaysDto.class)
                .block();
    }
}
