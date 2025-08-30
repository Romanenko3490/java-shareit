package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для представления пользователя.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    /**
     * Идентификатор пользователя.
     */
    private Long id;

    /**
     * Имя пользователя.
     */
    private String name;

    /**
     * Email пользователя.
     */
    private String email;
}
