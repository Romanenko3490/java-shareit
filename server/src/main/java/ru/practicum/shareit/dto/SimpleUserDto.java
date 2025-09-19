package ru.practicum.shareit.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleUserDto {

    private Long id;

    private String name;
}
