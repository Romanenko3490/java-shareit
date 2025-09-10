package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithReplaysDto;
import ru.practicum.shareit.request.dto.ReplyDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {
    ItemRequestMapper INSTANCE = Mappers.getMapper(ItemRequestMapper.class);

    ItemRequestDto toDto(ItemRequest itemRequest);

    @Mapping(source = "id", target = "itemId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "owner.id", target = "ownerId")
    ReplyDto toReplyDto(Item item);

    default ItemRequestWithReplaysDto toItemRequestWithRepliesDto(ItemRequest request, List<ReplyDto> replies) {
        ItemRequestWithReplaysDto dto = new ItemRequestWithReplaysDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        dto.setItems(replies);
        return dto;
    }

}
