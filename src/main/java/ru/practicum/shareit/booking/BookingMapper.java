package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

/**
 * Маппер для преобразования между сущностью Booking и DTO.
 * Использует MapStruct для автоматической генерации кода преобразования.
 *
 * <p>Поддерживает преобразование:</p>
 * <ul>
 *   <li>Booking → BookingDto (полное преобразование)</li>
 *   <li>Booking → BookingShortDto (краткое преобразование)</li>
 *   <li>List<Booking> → List<BookingShortDto> (пакетное преобразование)</li>
 * </ul>
 */
@Mapper(componentModel = "spring")
public interface BookingMapper {

    /**
     * Экземпляр маппера для использования в статическом контексте.
     */
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    /**
     * Преобразует сущность Booking в полное DTO представление.
     *
     * @param booking сущность бронирования
     * @return полное DTO бронирования со всей информацией
     *
     * @mapping source = "id" target = "id" - маппинг идентификатора
     * @mapping source = "startTime" target = "start" - преобразование времени начала
     * @mapping source = "endTime" target = "end" - преобразование времени окончания
     * @mapping source = "item" target = "item" - маппинг предмета бронирования
     * @mapping source = "booker" target = "booker" - маппинг пользователя
     * @mapping source = "bookingStatus" target = "status" - преобразование статуса бронирования
     */
    @Mapping(source = "id", target = "id")
    @Mapping(source = "startTime", target = "start")
    @Mapping(source = "endTime", target = "end")
    @Mapping(source = "item", target = "item")
    @Mapping(source = "booker", target = "booker")
    @Mapping(source = "bookingStatus", target = "status")
    BookingDto toDto(Booking booking);

    /**
     * Преобразует сущность Booking в краткое DTO представление.
     * Используется для вложенных представлений в других DTO.
     *
     * @param booking сущность бронирования
     * @return краткое DTO бронирования с базовой информацией
     *
     * @mapping source = "id" target = "id" - маппинг идентификатора бронирования
     * @mapping source = "booker.id" target = "bookerId" - маппинг только ID пользователя
     * @mapping source = "startTime" target = "start" - преобразование времени начала
     * @mapping source = "endTime" target = "end" - преобразование времени окончания
     */
    @Mapping(source = "id", target = "id")
    @Mapping(source = "booker.id", target = "bookerId")
    @Mapping(source = "startTime", target = "start")
    @Mapping(source = "endTime", target = "end")
    BookingShortDto toShortDto(Booking booking);

    /**
     * Преобразует список сущностей Booking в список кратких DTO.
     * Автоматически использует метод toShortDto для каждого элемента списка.
     *
     * @param bookingList список сущностей бронирования
     * @return список кратких DTO бронирований
     *
     * @see #toShortDto(Booking)
     */
    List<BookingShortDto> toShortDtos(List<Booking> bookingList);
}
