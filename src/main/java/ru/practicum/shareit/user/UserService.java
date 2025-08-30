package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

/**
 * Интерфейс сервиса для работы с пользователями.
 */
public interface UserService {
    /**
     * Добавляет нового пользователя.
     *
     * @param request данные пользователя
     * @return созданный пользователь
     */
    UserDto addUser(NewUserRequest request);

    /**
     * Обновляет существующего пользователя.
     *
     * @param userId  ID пользователя
     * @param request данные для обновления
     * @return обновленный пользователь
     */
    UserDto updateUser(Long userId, UpdateUserRequest request);

    /**
     * Получает пользователя по ID.
     *
     * @param userId ID пользователя
     * @return найденный пользователь
     */
    UserDto getUser(Long userId);

    /**
     * Удаляет пользователя по ID.
     *
     * @param userId ID пользователя
     */
    void deleteUser(Long userId);
}
