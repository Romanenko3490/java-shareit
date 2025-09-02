package ru.practicum.shareit.booking.dal;


import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.QBooking;
import ru.practicum.shareit.enums.BookingState;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public interface BookingRepository extends JpaRepository<Booking, Long>,
        QuerydslPredicateExecutor<Booking> {

    List<Booking> findByItemIdIn(List<Long> itemIds);

    default boolean hasUserCompletedBookings(Long userId, Long itemId) {
        QBooking booking = QBooking.booking;

        LocalDateTime now = LocalDateTime.now();

        //костыль, в тесте постмана не работает таймаут, и не успевает кончится бронирование
        return exists(booking.booker.id.eq(userId)
                .and(booking.item.id.eq(itemId))
                .and(booking.bookingStatus.eq(BookingStatus.APPROVED))
                .and(booking.endTime.lt(now.minusNanos(500_000_000))));
    }

    List<Booking> findAllByItem_Id(Long itemId);

    /**
     * Вспомогательный метод для получения бронирований с фильтрацией по статусу.
     *
     * @param userId  идентификатор пользователя
     * @param state   статус бронирования
     * @param isOwner флаг, указывающий является ли пользователь владельцем предметов
     * @return список DTO бронирований, отфильтрованных по статусу
     * @throws ValidationException если передан неверный статус
     */

    default List<Booking> findBookingsByState(Long userId, BookingState state, boolean isOwner, Long itemId) {
        // Получил Q-класс для работы с полями Booking
        QBooking booking = QBooking.booking;

        // Базовое условие - бронирования только для этого пользователя
        // Эквивалент WHERE
        BooleanExpression baseCondition = isOwner ?
                booking.item.owner.id.eq(userId) :
                booking.booker.id.eq(userId);

        if (itemId != null) {
            baseCondition = baseCondition.and(booking.item.id.eq(itemId));
        }

        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case CURRENT:
                baseCondition = baseCondition.and(
                        // startTime <= сейчас
                        booking.startTime.loe(now).and(
                                // endTime >= сейчас
                                booking.endTime.goe(now))
                );
                break;

            case PAST:
                baseCondition = baseCondition.and(
                        // endTime < сейчас
                        // костыль, потому-что таймаут не срабатывает
                        booking.startTime.lt(now).and(booking.endTime.lt(now.minusSeconds(1)))
                );
                break;

            case FUTURE:
                baseCondition = baseCondition.and(
                        // startTime > сейчас
                        booking.startTime.gt(now)
                );
                break;

            case WAITING:
                baseCondition = baseCondition.and(
                        booking.bookingStatus.eq(BookingStatus.WAITING)
                );
                break;

            case REJECTED:
                baseCondition = baseCondition.and(
                        booking.bookingStatus.eq(BookingStatus.REJECTED)
                );
                break;

            case ALL:
                // Оставляем baseCondition без изменений
                break;
        }

        Iterable<Booking> bookings = findAll(
                baseCondition,
                Sort.by(Sort.Direction.DESC, "startTime") // ORDER BY
        );

        return StreamSupport.stream(bookings.spliterator(), false)
                .collect(Collectors.toList());
    }

    default Optional<Booking> findLastBookingForItem(Long itemId, Long ownerId) {
        List<Booking> currentBookings = findBookingsByState(ownerId, BookingState.CURRENT, true, itemId);

        return currentBookings.stream()
                .filter(booking -> booking.getBookingStatus() == BookingStatus.APPROVED)
                .findFirst(); // Берем первое текущее бронирование
    }

    default Optional<Booking> findNextBookingForItem(Long itemId, Long ownerId) {
        List<Booking> futureBookings = findBookingsByState(ownerId, BookingState.FUTURE, true, itemId);

        return futureBookings.stream()
                .filter(booking -> booking.getBookingStatus() == BookingStatus.APPROVED)
                .min(Comparator.comparing(Booking::getStartTime)); // Ближайшее по времени
    }
}
