package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jdk.jfr.BooleanFlag;
import lombok.Data;

@Data
public class NewItemRequest {

    @NotBlank(message = "Name cannot be Empty")
    private String name;

    @NotBlank(message = "Description cannot be Empty")
    private String description;

    @BooleanFlag
    @NotNull
    private Boolean available;

    private String request;

}
