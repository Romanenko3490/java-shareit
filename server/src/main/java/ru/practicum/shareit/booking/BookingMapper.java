package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;


@Mapper(componentModel = "spring")
public interface BookingMapper {


    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);


    @Mapping(source = "id", target = "id")
    @Mapping(source = "startTime", target = "start")
    @Mapping(source = "endTime", target = "end")
    @Mapping(source = "item", target = "item")
    @Mapping(source = "booker", target = "booker")
    @Mapping(source = "bookingStatus", target = "status")
    BookingDto toDto(Booking booking);


    @Mapping(source = "id", target = "id")
    @Mapping(source = "booker.id", target = "bookerId")
    @Mapping(source = "startTime", target = "start")
    @Mapping(source = "endTime", target = "end")
    BookingShortDto toShortDto(Booking booking);


    List<BookingShortDto> toShortDtos(List<Booking> bookingList);
}
