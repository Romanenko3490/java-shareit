//package ru.practicum.shareit.util;
//
//import ru.practicum.shareit.booking.dto.BookingDto;
//import ru.practicum.shareit.booking.dto.BookingShortDto;
//import ru.practicum.shareit.item.dto.ItemBookingDto;
//import ru.practicum.shareit.booking.dto.UserBookingDto;
//import ru.practicum.shareit.booking.model.Booking;
//import ru.practicum.shareit.item.comments.dto.CommentDto;
//import ru.practicum.shareit.item.comments.model.Comment;
//import ru.practicum.shareit.item.dto.ItemDto;
//import ru.practicum.shareit.item.dto.ItemLastNextBookingsAndCommentsDto;
//import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
//import ru.practicum.shareit.item.model.Item;
//import ru.practicum.shareit.user.model.User;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * Утилитарный класс для преобразования Entity объектов в DTO.
// * Предоставляет статические методы для маппинга между слоями приложения.
// */
//public class GlobalMapper {
//
//    /**
//     * Приватный конструктор для предотвращения создания экземпляров утилитарного класса.
//     */
//    private GlobalMapper() {
//        // Утилитарный класс не должен иметь публичных конструкторов
//    }
//
//    /**
//     * Преобразует сущность Item в ItemDto.
//     * Включает идентификатор владельца, если он установлен.
//     *
//     * @param item сущность предмета
//     * @return DTO предмета
//     */
//    public static ItemDto toDto(Item item) {
//        return ItemDto.builder()
//                .id(item.getId())
//                .name(item.getName())
//                .description(item.getDescription())
//                .ownerId(item.getOwner() != null ? item.getOwner().getId() : null)
//                .available(item.getAvailable())
//                .build();
//    }
//
//    /**
//     * Преобразует сущность Booking в BookingDto.
//     * Включает преобразованные DTO для связанных сущностей Item и User.
//     *
//     * @param booking сущность бронирования
//     * @return DTO бронирования
//     * @throws NullPointerException если статус бронирования равен null
//     */
//    public static BookingDto toDto(Booking booking) {
//        return BookingDto.builder()
//                .id(booking.getId())
//                .start(booking.getStartTime())
//                .end(booking.getEndTime())
//                .item(GlobalMapper.toItemBookingDto(booking.getItem()))
//                .booker(GlobalMapper.toUserBookingDto(booking.getBooker()))
//                .status(booking.getBookingStatus())
//                .build();
//    }
//
//    /**
//     * Преобразует сущность Item в ItemBookingDto.
//     *
//     * @param item сущность предмета
//     * @return DTO предмета для бронирования
//     */
//    public static ItemBookingDto toItemBookingDto(Item item) {
//        return new ItemBookingDto(item.getId(), item.getName());
//    }
//
//    /**
//     * Преобразует сущность User в UserBookingDto.
//     *
//     * @param user сущность пользователя
//     * @return DTO пользователя для бронирования
//     */
//    public static UserBookingDto toUserBookingDto(User user) {
//        return new UserBookingDto(user.getId(), user.getName());
//    }
//
//    /**
//     * Преобразует сущность Booking в BookingShortDto.
//     * Возвращает null, если передан null.
//     *
//     * @param booking сущность бронирования или null
//     * @return DTO краткой информации о бронировании или null
//     */
//    public static BookingShortDto toShortDto(Booking booking) {
//        if (booking == null) {
//            return null;
//        }
//        return new BookingShortDto(
//                booking.getId(),
//                booking.getBooker().getId(),
//                booking.getStartTime(),
//                booking.getEndTime()
//        );
//    }
//
//    /**
//     * Преобразует сущность Item в ItemWithBookingsDto с информацией о бронированиях и комментариях.
//     *
//     * @param item      сущность предмета
//     * @param bookings  список бронирований предмета
//     * @param comments  список комментариев к предмету
//     * @return DTO предмета с информацией о бронированиях
//     */
//    public static ItemWithBookingsDto toItemWithBookingsDto(Item item,
//                                                            List<Booking> bookings,
//                                                            List<Comment> comments) {
//        List<BookingShortDto> bookingDtos = bookings.stream()
//                .map(GlobalMapper::toShortDto)
//                .collect(Collectors.toList());
//
//        List<CommentDto> commentDtos = comments.stream()
//                .map(GlobalMapper::toCommentDto)
//                .collect(Collectors.toList());
//
//        return ItemWithBookingsDto.builder()
//                .id(item.getId())
//                .name(item.getName())
//                .description(item.getDescription())
//                .available(item.getAvailable())
//                .bookings(bookingDtos)
//                .comments(commentDtos)
//                .build();
//    }
//
//    /**
//     * Преобразует сущность Comment в CommentDto.
//     *
//     * @param comment сущность комментария
//     * @return DTO комментария
//     */
//    public static CommentDto toCommentDto(Comment comment) {
//        return new CommentDto(comment.getId(),
//                comment.getText(),
//                comment.getAuthor().getName(),
//                comment.getCreated());
//    }
//
//    /**
//     * Преобразует сущность Item в ItemLastNextBookingsAndCommentsDto
//     * с информацией о последнем/следующем бронировании и комментариях.
//     *
//     * @param item         сущность предмета
//     * @param lastBooking  последнее завершенное бронирование
//     * @param nextBooking  следующее бронирование
//     * @param comments     список комментариев к предмету
//     * @return DTO предмета с детальной информацией о бронированиях
//     */
//    public static ItemLastNextBookingsAndCommentsDto toItemLastNextBookingsAndCommentsDto(Item item,
//                                                                                          BookingShortDto lastBooking,
//                                                                                          BookingShortDto nextBooking,
//                                                                                          List<Comment> comments) {
//
//        List<CommentDto> commentDtos = comments.stream()
//                .map(GlobalMapper::toCommentDto)
//                .collect(Collectors.toList());
//
//        return ItemLastNextBookingsAndCommentsDto.builder()
//                .id(item.getId())
//                .name(item.getName())
//                .description(item.getDescription())
//                .available(item.getAvailable())
//                .lastBooking(lastBooking)
//                .nextBooking(nextBooking)
//                .comments(commentDtos)
//                .build();
//    }
//}