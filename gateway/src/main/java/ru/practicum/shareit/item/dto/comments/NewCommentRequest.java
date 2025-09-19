package ru.practicum.shareit.item.dto.comments;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCommentRequest {

    @NotBlank(message = "Comment can not be empty")
    private String text;
}
