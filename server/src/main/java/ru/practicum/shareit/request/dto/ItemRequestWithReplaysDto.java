package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class ItemRequestWithReplaysDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ReplyDto> items;
}
