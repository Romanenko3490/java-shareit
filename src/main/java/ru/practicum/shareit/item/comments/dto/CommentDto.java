package ru.practicum.shareit.item.comments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    @NotNull
    private Long id;

    @NotBlank
    @Size(max = 1000, message = "Comment cannot be longer than 1000 characters")
    private String text;

    @NotBlank
    private String authorName;

    @NotNull
    private LocalDateTime created;

}
