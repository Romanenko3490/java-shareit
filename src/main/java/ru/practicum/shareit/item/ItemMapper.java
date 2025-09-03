package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.comments.dto.CommentDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * Маппер для преобразования между сущностью Item и различными DTO представлениями.
 * Обеспечивает гибкое преобразование предмета в зависимости от требуемого уровня детализации.
 *
 * <p>Поддерживает несколько вариантов преобразования:</p>
 * <ul>
 *   <li>Базовое преобразование с бронированиями и комментариями</li>
 *   <li>Полное DTO с информацией о владельце</li>
 *   <li>Краткое представление для бронирований</li>
 *   <li>Упрощенное представление с переименованными полями</li>
 *   <li>Представление с списками бронирований и комментариев</li>
 * </ul>
 */
@Mapper(componentModel = "spring")
public interface ItemMapper {

    /**
     * Экземпляр маппера для использования в статическом контексте.
     */
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    /**
     * Базовое преобразование сущности Item в DTO с бронированиями и комментариями.
     * Используется как основа для дальнейшего дополнения данными.
     *
     * @param item сущность предмета
     * @return базовое DTO с основными полями предмета
     *
     * @mapping source = "id" target = "id" - маппинг идентификатора предмета
     * @mapping source = "name" target = "name" - маппинг названия предмета
     * @mapping source = "description" target = "description" - маппинг описания предмета
     * @mapping source = "available" target = "available" - маппинг статуса доступности
     */
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "available", target = "available")
    ItemLastNextBookingsAndCommentsDto toItemBaseDto(Item item);

    /**
     * Полное преобразование сущности Item в DTO с информацией о ближайших бронированиях и комментариями.
     * Дополняет базовое DTO данными о последнем, следующем бронировании и комментариями.
     *
     * @param item сущность предмета
     * @param lastBooking DTO последнего бронирования (может быть null)
     * @param nextBooking DTO следующего бронирования (может быть null)
     * @param comments список DTO комментариев (может быть null)
     * @return полное DTO предмета с всей информацией
     */
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

    /**
     * Преобразует сущность Item в полное DTO представление с информацией о владельце.
     * Включает идентификатор владельца для отображения информации о собственнике.
     *
     * @param item сущность предмета
     * @return полное DTO предмета с информацией о владельце
     *
     * @mapping source = "id" target = "id" - маппинг идентификатора предмета
     * @mapping source = "name" target = "name" - маппинг названия предмета
     * @mapping source = "description" target = "description" - маппинг описания предмета
     * @mapping source = "owner.id" target = "ownerId" - маппинг идентификатора владельца
     * @mapping source = "available" target = "available" - маппинг статуса доступности
     */
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "available", target = "available")
    ItemDto toDto(Item item);

    /**
     * Преобразует сущность Item в DTO для использования в контексте бронирований.
     * Содержит только базовую информацию, необходимую для отображения в бронированиях.
     *
     * @param item сущность предмета
     * @return DTO предмета для бронирований
     *
     * @mapping target = "id" source = "id" - маппинг идентификатора предмета
     * @mapping target = "name" source = "name" - маппинг названия предмета
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ItemBookingDto toItemBookingDto(Item item);

    /**
     * Преобразует сущность Item в краткое DTO представление с переименованными полями.
     * Используется для специфических случаев, где требуется иное именование полей.
     *
     * @param item сущность предмета
     * @return краткое DTO предмета с переименованными полями
     *
     * @mapping source = "id" target = "id" - маппинг идентификатора предмета
     * @mapping source = "name" target = "name" - маппинг названия предмета
     * @mapping source = "description" target = "description" - маппинг описания предмета
     * @mapping source = "available" target = "isActive" - маппинг статуса доступности с переименованием
     */
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "available", target = "isActive")
    ItemShortDto tiShortDto(Item item);

    /**
     * Базовое преобразование сущности Item в DTO со списками бронирований и комментариев.
     * Используется как основа для дальнейшего дополнения списками данных.
     *
     * @param item сущность предмета
     * @return базовое DTO с основными полями предмета
     *
     * @mapping source = "id" target = "id" - маппинг идентификатора предмета
     * @mapping source = "name" target = "name" - маппинг названия предмета
     * @mapping source = "description" target = "description" - маппинг описания предмета
     * @mapping source = "available" target = "available" - маппинг статуса доступности
     */
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "available", target = "available")
    ItemWithBookingsDto toItemWithBookingsBaseDto(Item item);

    /**
     * Полное преобразование сущности Item в DTO со списками бронирований и комментариев.
     * Дополняет базовое DTO списками бронирований и комментариев.
     *
     * @param item сущность предмета
     * @param bookings список DTO бронирований (может быть null)
     * @param comments список DTO комментариев (может быть null)
     * @return полное DTO предмета со списками бронирований и комментариев
     */
    default ItemWithBookingsDto toItemWithBookingDto(Item item,
                                                     List<BookingShortDto> bookings,
                                                     List<CommentDto> comments) {
        ItemWithBookingsDto dto = toItemWithBookingsBaseDto(item);
        dto.setBookings(bookings != null ? bookings : List.of());
        dto.setComments(comments != null ? comments : List.of());
        return dto;
    }
}
