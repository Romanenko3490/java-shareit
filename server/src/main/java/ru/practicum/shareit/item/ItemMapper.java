package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.comments.dto.CommentDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {


    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);


    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "available", target = "available")
    ItemLastNextBookingsAndCommentsDto toItemBaseDto(Item item);

    default ItemLastNextBookingsAndCommentsDto toFullDto(Item item,
                                                         BookingShortDto lastBooking,
                                                         BookingShortDto nextBooking,
                                                         List<CommentDto> comments) {
        ItemLastNextBookingsAndCommentsDto dto = toItemBaseDto(item);
        dto.setLastBooking(lastBooking);
        dto.setNextBooking(nextBooking);
        dto.setComments(comments != null ? comments : List.of());
        return dto;
    }


    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "available", target = "available")
    ItemDto toDto(Item item);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ItemBookingDto toItemBookingDto(Item item);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "available", target = "isActive")
    ItemShortDto tiShortDto(Item item);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "available", target = "available")
    ItemWithBookingsDto toItemWithBookingsBaseDto(Item item);


    default ItemWithBookingsDto toItemWithBookingDto(Item item,
                                                     List<BookingShortDto> bookings,
                                                     List<CommentDto> comments) {
        ItemWithBookingsDto dto = toItemWithBookingsBaseDto(item);
        dto.setBookings(bookings != null ? bookings : List.of());
        dto.setComments(comments != null ? comments : List.of());
        return dto;
    }
}
