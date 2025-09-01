package ru.practicum.shareit.booking;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.GlobalMapper;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.QBooking;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Сервис для управления операциями бронирования.
 * Обеспечивает создание, обновление и получение информации о бронированиях.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    /**
     * Создает новое бронирование.
     *
     * @param bookerId идентификатор пользователя, осуществляющего бронирование
     * @param request  DTO с данными для создания бронирования
     * @return DTO созданного бронирования
     * @throws NotFoundException если пользователь или предмет не найдены
     */
    public BookingDto addBooking(Long bookerId, NewBookingRequest request) {
        log.debug("addBooking bookerId={}, request={}", bookerId, request);
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new NotFoundException("Item for book not found"));

        if (!item.getAvailable()) {
            throw new ValidationException("Item is not available for booking");
        }

        Booking booking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .startTime(request.getStart())
                .endTime(request.getEnd())
                .item(item)
                .bookingStatus(BookingStatus.WAITING)
                .build());
        log.debug("booking={}", booking);

        return GlobalMapper.toDto(booking);
    }

    /**
     * Обновляет статус бронирования (подтверждение/отклонение).
     *
     * @param userId    идентификатор владельца предмета
     * @param bookingId идентификатор бронирования
     * @param approved  флаг подтверждения (true - подтверждено, false - отклонено)
     * @return DTO обновленного бронирования
     * @throws NotFoundException   если бронирование не найдено
     * @throws ValidationException если пользователь не является владельцем предмета
     */
    public BookingDto updateBookingStatus(Long userId, Long bookingId, Boolean approved) {
        log.debug("updateBooking bookerId={}, bookingId={}", bookingId, userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException("User not owner of booking");
        }

        if (approved) {
            booking.setBookingStatus(BookingStatus.APPROVED);
        } else {
            booking.setBookingStatus(BookingStatus.REJECTED);
        }

        Booking updatedBooking = bookingRepository.save(booking);
        log.debug("updated booking={}", updatedBooking);
        return GlobalMapper.toDto(updatedBooking);
    }

    /**
     * Получает информацию о бронировании по идентификатору.
     *
     * @param userId    идентификатор пользователя, запрашивающего информацию
     * @param bookingId идентификатор бронирования
     * @return DTO бронирования
     * @throws NotFoundException   если бронирование не найдено
     * @throws ValidationException если пользователь не является ни владельцем, ни автором бронирования
     */
    public BookingDto getBookingById(Long userId, Long bookingId) {
        log.debug("getBooking bookerId={}, bookingId={}", bookingId, userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        boolean isOwner = booking.getItem().getOwner().getId().equals(userId);
        boolean isBooker = booking.getBooker().getId().equals(userId);

        if (!isOwner && !isBooker) {
            throw new ValidationException("User not owner or author of booking");
        }

        return GlobalMapper.toDto(booking);
    }

    /**
     * Получает список бронирований пользователя с фильтрацией по статусу.
     *
     * @param userId идентификатор пользователя
     * @param state  статус бронирования (ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED)
     * @return список DTO бронирований
     */
    public List<BookingDto> getUserBookings(Long userId, String state) {
        return getBookingsAsState(userId, state, false);
    }

    /**
     * Получает список бронирований для предметов владельца с фильтрацией по статусу.
     *
     * @param ownerId идентификатор владельца предметов
     * @param state   статус бронирования (ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED)
     * @return список DTO бронирований
     */
    @Transactional(readOnly = true)
    public List<BookingDto> getOwnerBookings(Long ownerId, String state) {
        return getBookingsAsState(ownerId, state, true);
    }

    /**
     * Вспомогательный метод для получения бронирований с фильтрацией по статусу.
     *
     * @param userId  идентификатор пользователя
     * @param state   статус бронирования
     * @param isOwner флаг, указывающий является ли пользователь владельцем предметов
     * @return список DTO бронирований, отфильтрованных по статусу
     * @throws ValidationException если передан неверный статус
     */
    private List<BookingDto> getBookingsAsState(Long userId, String state, boolean isOwner) {
        log.debug("getUserBookings bookerId={}, state={}", userId, state);
        if (!userRepository.findById(userId).isPresent()) {
            throw new NotFoundException("User not found");
        }
        // Получил Q-класс для работы с полями Booking
        QBooking booking = QBooking.booking;

        // Базовое условие - бронирования только для этого пользователя
        // Эквивалент WHERE
        BooleanExpression baseCondition = isOwner ?
                booking.item.owner.id.eq(userId) :
                booking.booker.id.eq(userId);

        switch (state.toUpperCase()) {
            case "CURRENT":
                baseCondition = baseCondition.and(
                        // startTime <= сейчас
                        booking.startTime.loe(LocalDateTime.now()).and(
                                // endTime >= сейчас
                                booking.endTime.goe(LocalDateTime.now()))
                );
                break;

            case "PAST":
                baseCondition = baseCondition.and(
                        // endTime < сейчас
                        booking.endTime.lt(LocalDateTime.now())
                );
                break;

            case "FUTURE":
                baseCondition = baseCondition.and(
                        // startTime > сейчас
                        booking.startTime.gt(LocalDateTime.now())
                );
                break;

            case "WAITING":
                baseCondition = baseCondition.and(
                        booking.bookingStatus.eq(BookingStatus.WAITING)
                );
                break;

            case "REJECTED":
                baseCondition = baseCondition.and(
                        booking.bookingStatus.eq(BookingStatus.REJECTED)
                );
                break;

            case "ALL":
                // Оставляем baseCondition без изменений
                break;

            default:
                throw new ValidationException("Invalid state " + state);
        }

        Iterable<Booking> bookings = bookingRepository.findAll(
                baseCondition,
                Sort.by(Sort.Direction.DESC, "startTime") // ORDER BY
        );

        return StreamSupport.stream(bookings.spliterator(), false)
                .map(GlobalMapper::toDto)
                .collect(Collectors.toList());
    }
}
