package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public UserDto createUser(@RequestBody @Valid NewUserRequest request) {
        return userClient.createUser(request);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable @Min(1) Long userId,
                              @RequestBody @Valid UpdateUserRequest request) {
        return userClient.updateUser(userId, request);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable @Min(1) Long userId) {
        return userClient.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable @Min(1) Long userId) {
        userClient.deleteUser(userId);
    }
}