package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.BookingState;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для управления операциями бронирования.
 * Обеспечивает создание, обновление и получение информации о бронированиях.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingService {

    /**
     * Репозиторий для работы с бронированиями.
     */
    private final BookingRepository bookingRepository;

    /**
     * Репозиторий для работы с пользователями.
     */
    private final UserRepository userRepository;

    /**
     * Репозиторий для работы с вещами.
     */
    private final ItemRepository itemRepository;

    /**
     * Маппер.
     */
    private final BookingMapper bookingMapper;

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

        return bookingMapper.toDto(booking);
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
        return bookingMapper.toDto(booking);
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
    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long userId, Long bookingId) {
        log.debug("getBooking bookerId={}, bookingId={}", bookingId, userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        boolean isOwner = booking.getItem().getOwner().getId().equals(userId);
        boolean isBooker = booking.getBooker().getId().equals(userId);

        if (!isOwner && !isBooker) {
            throw new ValidationException("User not owner or author of booking");
        }

        return bookingMapper.toDto(booking);
    }

    /**
     * Получает список бронирований пользователя с фильтрацией по статусу.
     *
     * @param userId идентификатор пользователя
     * @param state  статус бронирования (ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED)
     * @return список DTO бронирований
     */
    @Transactional(readOnly = true)
    public List<BookingDto> getUserBookings(Long userId, BookingState state) {
        if (!userRepository.findById(userId).isPresent()) {
            throw new NotFoundException("User not found");
        }

        return bookingRepository.findBookingsByState(userId, state, false, null)
                .stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Получает список бронирований для предметов владельца с фильтрацией по статусу.
     *
     * @param ownerId идентификатор владельца предметов
     * @param state   статус бронирования (ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED)
     * @return список DTO бронирований
     */
    @Transactional(readOnly = true)
    public List<BookingDto> getOwnerBookings(Long ownerId, BookingState state) {
        if (!userRepository.findById(ownerId).isPresent()) {
            throw new NotFoundException("User not found");
        }
        return bookingRepository.findBookingsByState(ownerId, state, true, null)
                .stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

}
