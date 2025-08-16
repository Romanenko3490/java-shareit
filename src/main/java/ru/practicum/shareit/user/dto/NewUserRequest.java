package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewUserRequest {

    @NotBlank(message = "Empty name not allowed")
    private String name;

    @NotBlank(message = "Empty Email not allowed")
    @Email(message = "Wrong Email format")
    private String email;


}
