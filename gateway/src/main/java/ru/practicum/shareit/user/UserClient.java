package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.base.BaseWebClient;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

@Service
public class UserClient extends BaseWebClient {
    private static final String API_PREFIX = "/users";

        public UserClient(@Value("${shareit-server.url}") String serverUrl) {
        super(serverUrl, API_PREFIX);
    }

    public UserDto createUser(NewUserRequest request) {
        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();
    }

    public UserDto updateUser(Long userId, UpdateUserRequest request) {
        return webClient.patch()
                .uri("/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();
    }

    public UserDto getUser(Long userId) {
        return webClient.get()
                .uri("/" + userId)
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();

    }

    public void deleteUser(Long userId) {
        webClient.delete()
                .uri("/" + userId)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

}
