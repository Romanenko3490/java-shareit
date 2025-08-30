package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

/**
 * Контроллер для работы с пользователями.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    /**
     * Сервис для работы с пользователями.
     */
    private final UserService userService;

    /**
     * Создает нового пользователя.
     *
     * @param request данные пользователя
     * @return созданный пользователь
     */
    @PostMapping
    public UserDto createUser(@RequestBody
                              @Valid
                              final NewUserRequest request) {
        return userService.addUser(request);
    }

    /**
     * Обновляет существующего пользователя.
     *
     * @param userId  ID пользователя
     * @param request данные для обновления
     * @return обновленный пользователь
     */
    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable
                              @Min(1) final Long userId,
                              @RequestBody
                              @Valid final UpdateUserRequest request) {
        return userService.updateUser(userId, request);
    }

    /**
     * Получает пользователя по ID.
     *
     * @param userId ID пользователя
     * @return найденный пользователь
     */
    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable
                           @Min(1) final Long userId) {
        return userService.getUser(userId);
    }

    /**
     * Удаляет пользователя по ID.
     *
     * @param userId ID пользователя
     */
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable
                           @Min(1) final Long userId) {
        userService.deleteUser(userId);
    }
}
