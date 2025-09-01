package ru.practicum.shareit;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

/**
 * Утилитарный класс для преобразования Entity объектов в DTO.
 * Предоставляет статические методы для маппинга между слоями приложения.
 */
public class GlobalMapper {

    /**
     * Приватный конструктор для предотвращения создания экземпляров утилитарного класса.
     */
    private GlobalMapper() {
        // Утилитарный класс не должен иметь публичных конструкторов
    }

    /**
     * Преобразует сущность User в UserDto.
     *
     * @param user сущность пользователя
     * @return DTO пользователя
     */
    public static UserDto toDto(User user) {
        UserDto userDto = new UserDto(user.getId(),
                user.getName(),
                user.getEmail());
        return userDto;
    }

    /**
     * Преобразует сущность Item в ItemDto.
     * Включает идентификатор владельца, если он установлен.
     *
     * @param item сущность предмета
     * @return DTO предмета
     */
    public static ItemDto toDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .ownerId(item.getOwner() != null ? item.getOwner().getId() : null)
                .available(item.getAvailable())
                .build();
    }

    /**
     * Преобразует сущность Booking в BookingDto.
     * Включает преобразованные DTO для связанных сущностей Item и User.
     *
     * @param booking сущность бронирования
     * @return DTO бронирования
     * @throws NullPointerException если статус бронирования равен null
     */
    public static BookingDto toDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStartTime())
                .end(booking.getEndTime())
                .item(GlobalMapper.toDto(booking.getItem()))
                .booker(GlobalMapper.toDto(booking.getBooker()))
                .status(booking.getBookingStatus().toString())
                .build();
    }
}