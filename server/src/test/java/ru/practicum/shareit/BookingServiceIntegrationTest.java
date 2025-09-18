package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class BookingServiceIntegrationTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @MockBean
    private BookingMapper bookingMapper;

    private BookingService bookingService;
    private User owner;
    private User booker;
    private Item availableItem;
    private Item unavailableItem;

    @BeforeEach
    void setUp() {
        bookingService = new BookingService(bookingRepository, userRepository, itemRepository, bookingMapper);

        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        owner = userRepository.save(User.builder()
                .name("Owner")
                .email("owner@email.com")
                .build());

        booker = userRepository.save(User.builder()
                .name("Booker")
                .email("booker@email.com")
                .build());

        availableItem = itemRepository.save(Item.builder()
                .name("Available Item")
                .description("Test available item")
                .available(true)
                .owner(owner)
                .build());

        unavailableItem = itemRepository.save(Item.builder()
                .name("Unavailable Item")
                .description("Test unavailable item")
                .available(false)
                .owner(owner)
                .build());

        when(bookingMapper.toDto(any())).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            return BookingDto.builder()
                    .id(booking.getId())
                    .start(booking.getStartTime())
                    .end(booking.getEndTime())
                    .status(booking.getBookingStatus())
                    .build();
        });
    }

    @Test
    void addBooking_shouldCreateBookingSuccessfully() {
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(availableItem.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto result = bookingService.addBooking(booker.getId(), request);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(request.getStart(), result.getStart());
        assertEquals(request.getEnd(), result.getEnd());
        assertEquals(BookingStatus.WAITING, result.getStatus());

        assertTrue(bookingRepository.existsById(result.getId()));
    }

    @Test
    void addBooking_shouldThrowExceptionWhenUserNotFound() {
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(availableItem.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        assertThrows(NotFoundException.class, () -> {
            bookingService.addBooking(999L, request);
        });
    }

    @Test
    void addBooking_shouldThrowExceptionWhenItemNotFound() {
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(999L);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        assertThrows(NotFoundException.class, () -> {
            bookingService.addBooking(booker.getId(), request);
        });
    }

    @Test
    void addBooking_shouldThrowExceptionWhenItemNotAvailable() {
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(unavailableItem.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        assertThrows(ValidationException.class, () -> {
            bookingService.addBooking(booker.getId(), request);
        });
    }

    @Test
    void updateBookingStatus_shouldApproveBookingSuccessfully() {
        Booking booking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(availableItem)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(2))
                .bookingStatus(BookingStatus.WAITING)
                .build());

        BookingDto result = bookingService.updateBookingStatus(owner.getId(), booking.getId(), true);

        assertNotNull(result);
        assertEquals(BookingStatus.APPROVED, result.getStatus());

        Booking updatedBooking = bookingRepository.findById(booking.getId()).orElseThrow();
        assertEquals(BookingStatus.APPROVED, updatedBooking.getBookingStatus());
    }

    @Test
    void updateBookingStatus_shouldRejectBookingSuccessfully() {
        Booking booking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(availableItem)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(2))
                .bookingStatus(BookingStatus.WAITING)
                .build());

        BookingDto result = bookingService.updateBookingStatus(owner.getId(), booking.getId(), false);

        assertNotNull(result);
        assertEquals(BookingStatus.REJECTED, result.getStatus());

        Booking updatedBooking = bookingRepository.findById(booking.getId()).orElseThrow();
        assertEquals(BookingStatus.REJECTED, updatedBooking.getBookingStatus());
    }

    @Test
    void updateBookingStatus_shouldThrowExceptionWhenNotOwner() {
        Booking booking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(availableItem)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(2))
                .bookingStatus(BookingStatus.WAITING)
                .build());

        assertThrows(ValidationException.class, () -> {
            bookingService.updateBookingStatus(booker.getId(), booking.getId(), true);
        });
    }

    @Test
    void getBookingById_shouldReturnBookingForOwner() {
        Booking booking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(availableItem)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(2))
                .bookingStatus(BookingStatus.WAITING)
                .build());

        BookingDto result = bookingService.getBookingById(owner.getId(), booking.getId());

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
    }

    @Test
    void getBookingById_shouldReturnBookingForBooker() {
        Booking booking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(availableItem)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(2))
                .bookingStatus(BookingStatus.WAITING)
                .build());

        BookingDto result = bookingService.getBookingById(booker.getId(), booking.getId());

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
    }

    @Test
    void getBookingById_shouldThrowExceptionForUnauthorizedUser() {
        Booking booking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(availableItem)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(2))
                .bookingStatus(BookingStatus.WAITING)
                .build());

        User stranger = userRepository.save(User.builder()
                .name("Stranger")
                .email("stranger@email.com")
                .build());

        assertThrows(ValidationException.class, () -> {
            bookingService.getBookingById(stranger.getId(), booking.getId());
        });
    }

    @Test
    void getUserBookings_shouldReturnEmptyListWhenNoBookings() {
        List<BookingDto> result = bookingService.getUserBookings(booker.getId(), BookingState.ALL);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getOwnerBookings_shouldReturnEmptyListWhenNoBookings() {
        List<BookingDto> result = bookingService.getOwnerBookings(owner.getId(), BookingState.ALL);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getUserBookings_shouldThrowExceptionWhenUserNotFound() {
        assertThrows(NotFoundException.class, () -> {
            bookingService.getUserBookings(999L, BookingState.ALL);
        });
    }

    @Test
    void getOwnerBookings_shouldThrowExceptionWhenUserNotFound() {
        assertThrows(NotFoundException.class, () -> {
            bookingService.getOwnerBookings(999L, BookingState.ALL);
        });
    }
}