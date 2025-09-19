package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.dto.SimpleUserDto;
import ru.practicum.shareit.dto.UserDto;
import ru.practicum.shareit.user.model.User;


@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto toDto(User user);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    SimpleUserDto toUserBookingDto(User user);
}