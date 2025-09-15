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

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;


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
