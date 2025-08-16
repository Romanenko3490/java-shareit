package ru.practicum.shareit.user;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody @Valid NewUserRequest request) {
        return userService.addUser(request);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable @Min(1) Long userId,
                              @RequestBody @Valid UpdateUserRequest request) {
        return userService.updateUser(userId, request);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable @Min(1) Long userId) {
        return userService.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable @Min(1) Long userId) {
        userService.deleteUser(userId);
    }
}
