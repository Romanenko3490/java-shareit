package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

/**
 * Утилитарный класс для маппинга между DTO и сущностями пользователей.
 */

public final class UserMapper {
    /**
     * Приватный конструктор для утилитного класса.
     */
    private UserMapper() {
        // Утилитный класс
    }

    /**
     * Преобразует запрос в сущность пользователя.
     *
     * @param request запрос на создание пользователя
     * @return сущность пользователя
     */
    public static User mapToUser(final NewUserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        return user;
    }

    /**
     * Преобразует сущность пользователя в DTO.
     *
     * @param user сущность пользователя
     * @return DTO пользователя
     */
    public static UserDto mapToUserDto(final User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    /**
     * Обновляет сущность пользователя данными из запроса.
     *
     * @param user    сущность пользователя
     * @param request запрос на обновление
     * @return обновленная сущность пользователя
     */
    public static User updateUser(final User user,
                                  final UpdateUserRequest request) {
        if (request.hasEmail()) {
            user.setEmail(request.getEmail());
        }
        if (request.hasName()) {
            user.setName(request.getName());
        }
        return user;
    }
}
