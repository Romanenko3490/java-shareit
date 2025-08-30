package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

/**
 * DTO для обновления пользователя.
 */
@Data
public final class UpdateUserRequest {
    /**
     * Имя пользователя.
     */
    private String name;

    /**
     * Email пользователя.
     */
    @Email(message = "Wrong email format")
    private String email;

    /**
     * Проверяет, содержит ли запрос имя.
     *
     * @return true если имя присутствует и не пустое
     */
    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    /**
     * Проверяет, содержит ли запрос email.
     *
     * @return true если email присутствует и не пустой
     */
    public boolean hasEmail() {
        return !(email == null || email.isBlank());
    }
}
