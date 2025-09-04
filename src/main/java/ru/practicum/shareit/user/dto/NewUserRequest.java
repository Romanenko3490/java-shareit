package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO для создания нового пользователя.
 */
@Data
public class NewUserRequest {
    /**
     * Имя пользователя.
     */
    @NotBlank(message = "Empty name not allowed")
    private String name;

    /**
     * Email пользователя.
     */
    @NotBlank(message = "Empty Email not allowed")
    @Email(message = "Wrong Email format")
    private String email;
}
