package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.comments.CommentMapper;
import ru.practicum.shareit.item.comments.dal.CommentRepository;
import ru.practicum.shareit.item.comments.dto.CommentDto;
import ru.practicum.shareit.item.comments.dto.NewCommentRequest;
import ru.practicum.shareit.item.comments.model.Comment;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dal.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ItemServiceIntegrationTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;


    @MockBean
    private ItemMapper itemMapper;

    @MockBean
    private BookingMapper bookingMapper;

    @MockBean
    private CommentMapper commentMapper;

    private ItemService itemService;

    private User owner;
    private User booker;
    private Item item1;
    private Item item2;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        itemService = new ItemService(
                itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                itemRequestRepository,
                itemMapper,
                bookingMapper,
                commentMapper
        );

        commentRepository.deleteAll();
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();

        owner = userRepository.save(User.builder()
                .name("Owner User")
                .email("owner@example.com")
                .build());

        booker = userRepository.save(User.builder()
                .name("Booker User")
                .email("booker@example.com")
                .build());


        itemRequest = itemRequestRepository.save(new ItemRequest("Need a hammer", owner));


        item1 = itemRepository.save(Item.builder()
                .name("Hammer")
                .description("A strong hammer")
                .available(true)
                .owner(owner)
                .request(itemRequest)
                .build());

        item2 = itemRepository.save(Item.builder()
                .name("Screwdriver")
                .description("A precision screwdriver")
                .available(false)
                .owner(owner)
                .build());

        setupMappers();
    }

    private void setupMappers() {
        when(itemMapper.toDto(any(Item.class))).thenAnswer(invocation -> {
            Item item = invocation.getArgument(0);
            ItemDto dto = new ItemDto();
            dto.setId(item.getId());
            dto.setName(item.getName());
            dto.setDescription(item.getDescription());
            dto.setOwnerId(item.getOwner() != null ? item.getOwner().getId() : null);
            dto.setAvailable(item.getAvailable());
            dto.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);
            return dto;
        });

        when(itemMapper.toItemBaseDto(any(Item.class))).thenAnswer(invocation -> {
            Item item = invocation.getArgument(0);
            ItemLastNextBookingsAndCommentsDto dto = new ItemLastNextBookingsAndCommentsDto();
            dto.setId(item.getId());
            dto.setName(item.getName());
            dto.setDescription(item.getDescription());
            dto.setAvailable(item.getAvailable());
            // lastBooking, nextBooking, comments будут установлены отдельно в тестах
            return dto;
        });

        when(itemMapper.toFullDto(any(Item.class), any(BookingShortDto.class), any(BookingShortDto.class), any(List.class)))
                .thenAnswer(invocation -> {
                    Item item = invocation.getArgument(0);
                    BookingShortDto lastBooking = invocation.getArgument(1);
                    BookingShortDto nextBooking = invocation.getArgument(2);
                    List<CommentDto> comments = invocation.getArgument(3);

                    System.out.println("Mapping: item=" + item.getId() +
                            ", lastBooking=" + lastBooking +
                            ", nextBooking=" + nextBooking +
                            ", comments=" + comments.size());

                    ItemLastNextBookingsAndCommentsDto dto = itemMapper.toItemBaseDto(item);
                    dto.setLastBooking(lastBooking);
                    dto.setNextBooking(nextBooking);
                    dto.setComments(comments);
                    return dto;
                });

        when(itemMapper.toItemWithBookingsBaseDto(any(Item.class))).thenAnswer(invocation -> {
            Item item = invocation.getArgument(0);
            ItemWithBookingsDto dto = new ItemWithBookingsDto();
            dto.setId(item.getId());
            dto.setName(item.getName());
            dto.setDescription(item.getDescription());
            dto.setAvailable(item.getAvailable());
            return dto;
        });

        when(itemMapper.toItemWithBookingDto(any(Item.class), any(List.class), any(List.class)))
                .thenAnswer(invocation -> {
                    Item item = invocation.getArgument(0);
                    List<BookingShortDto> bookings = invocation.getArgument(1);
                    List<CommentDto> comments = invocation.getArgument(2);
                    ItemWithBookingsDto dto = itemMapper.toItemWithBookingsBaseDto(item);
                    dto.setBookings(bookings);
                    dto.setComments(comments);
                    return dto;
                });

        // BookingMapper
        when(bookingMapper.toShortDto(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            if (booking == null) return null;
            BookingShortDto dto = new BookingShortDto();
            dto.setId(booking.getId());
            dto.setBookerId(booking.getBooker() != null ? booking.getBooker().getId() : null);
            dto.setStart(booking.getStartTime());
            dto.setEnd(booking.getEndTime());
            return dto;
        });

        when(bookingMapper.toShortDtos(any(List.class))).thenAnswer(invocation -> {
            List<Booking> bookings = invocation.getArgument(0);
            return bookings.stream()
                    .map(b -> {
                        BookingShortDto dto = new BookingShortDto();
                        dto.setId(b.getId());
                        dto.setBookerId(b.getBooker() != null ? b.getBooker().getId() : null);
                        dto.setStart(b.getStartTime());
                        dto.setEnd(b.getEndTime());
                        return dto;
                    })
                    .toList();
        });

        when(commentMapper.toDto(any(Comment.class))).thenAnswer(invocation -> {
            Comment comment = invocation.getArgument(0);
            CommentDto dto = new CommentDto();
            dto.setId(comment.getId());
            dto.setText(comment.getText());
            dto.setAuthorName(comment.getAuthor() != null ? comment.getAuthor().getName() : null);
            dto.setCreated(comment.getCreated());
            return dto;
        });

        when(commentMapper.toDtos(any(List.class))).thenAnswer(invocation -> {
            List<Comment> comments = invocation.getArgument(0);
            return comments.stream()
                    .map(commentMapper::toDto) // Используем уже замоканный toDto
                    .toList();
        });
    }

    @Test
    void addItem_shouldCreateItemSuccessfully() {
        NewItemRequest newItemRequest = new NewItemRequest();
        newItemRequest.setName("Drill");
        newItemRequest.setDescription("Powerful drill");
        newItemRequest.setAvailable(true);
        newItemRequest.setRequestId(itemRequest.getId());

        ItemDto result = itemService.addItem(owner.getId(), newItemRequest);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Drill", result.getName());
        assertEquals("Powerful drill", result.getDescription());
        assertTrue(result.getAvailable());
        assertEquals(owner.getId(), result.getOwnerId());
        assertEquals(itemRequest.getId(), result.getRequestId());

        Optional<Item> savedItemOpt = itemRepository.findById(result.getId());
        assertTrue(savedItemOpt.isPresent());
        assertEquals("Drill", savedItemOpt.get().getName());
        assertEquals(owner.getId(), savedItemOpt.get().getOwner().getId());
        assertEquals(itemRequest.getId(), savedItemOpt.get().getRequest().getId());
    }

    @Test
    void addItem_shouldThrowNotFoundException_whenUserNotFound() {
        NewItemRequest newItemRequest = new NewItemRequest();
        newItemRequest.setName("Drill");
        newItemRequest.setDescription("Powerful drill");
        newItemRequest.setAvailable(true);

        assertThrows(NotFoundException.class, () -> {
            itemService.addItem(999L, newItemRequest);
        });
    }

    @Test
    void addItem_shouldThrowNotFoundException_whenRequestNotFound() {

        NewItemRequest newItemRequest = new NewItemRequest();
        newItemRequest.setName("Drill");
        newItemRequest.setDescription("Powerful drill");
        newItemRequest.setAvailable(true);
        newItemRequest.setRequestId(999L); // Non-existent request ID

        assertThrows(NotFoundException.class, () -> {
            itemService.addItem(owner.getId(), newItemRequest);
        });
    }

    @Test
    void updateItem_shouldUpdateItemSuccessfully() {

        UpdateItemRequest updateRequest = new UpdateItemRequest();
        updateRequest.setName("Updated Hammer");
        updateRequest.setDescription("An even stronger hammer");

        ItemDto result = itemService.updateItem(owner.getId(), item1.getId(), updateRequest);

        assertNotNull(result);
        assertEquals("Updated Hammer", result.getName());
        assertEquals("An even stronger hammer", result.getDescription());
        assertEquals(item1.getAvailable(), result.getAvailable());
        assertEquals(owner.getId(), result.getOwnerId());

        Optional<Item> updatedItemOpt = itemRepository.findById(item1.getId());
        assertTrue(updatedItemOpt.isPresent());
        assertEquals("Updated Hammer", updatedItemOpt.get().getName());
        assertEquals("An even stronger hammer", updatedItemOpt.get().getDescription());
        assertEquals(item1.getAvailable(), updatedItemOpt.get().getAvailable());
    }

    @Test
    void updateItem_shouldThrowNotFoundException_whenItemNotFound() {
        UpdateItemRequest updateRequest = new UpdateItemRequest();
        updateRequest.setName("Updated Item");

        assertThrows(NotFoundException.class, () -> {
            itemService.updateItem(owner.getId(), 999L, updateRequest);
        });
    }

    @Test
    void updateItem_shouldThrowNotFoundException_whenUserIsNotOwner() {
        UpdateItemRequest updateRequest = new UpdateItemRequest();
        updateRequest.setName("Updated Item");

        assertThrows(NotFoundException.class, () -> {
            // booker tries to update owner's item
            itemService.updateItem(booker.getId(), item1.getId(), updateRequest);
        });
    }

    @Test
    void getItem_shouldReturnItemWithBookingsAndComments() {
        Booking currentBooking = bookingRepository.save(Booking.builder()
                .item(item1)
                .booker(booker)
                .startTime(LocalDateTime.now().minusHours(1))
                .endTime(LocalDateTime.now().plusHours(1))
                .bookingStatus(ru.practicum.shareit.enums.BookingStatus.APPROVED)
                .build());

        Booking futureBooking = bookingRepository.save(Booking.builder()
                .item(item1)
                .booker(booker)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(2))
                .bookingStatus(ru.practicum.shareit.enums.BookingStatus.APPROVED)
                .build());

        Comment comment = commentRepository.save(Comment.builder()
                .item(item1)
                .author(booker)
                .text("Great item!")
                .created(LocalDateTime.now())
                .build());


        ItemLastNextBookingsAndCommentsDto result = itemService.getItem(item1.getId());

        assertNotNull(result);
        assertEquals(item1.getId(), result.getId());
        assertEquals(item1.getName(), result.getName());

        assertNotNull(result.getLastBooking());
        assertEquals(currentBooking.getId(), result.getLastBooking().getId());

        assertNotNull(result.getNextBooking());
        assertEquals(futureBooking.getId(), result.getNextBooking().getId());

        assertNotNull(result.getComments());
        assertEquals(1, result.getComments().size());
        assertEquals("Great item!", result.getComments().get(0).getText());
    }

    @Test
    void getItem_shouldThrowNotFoundException_whenItemNotFound() {

        assertThrows(NotFoundException.class, () -> {
            itemService.getItem(999L);
        });
    }

    @Test
    void getUserItems_shouldReturnItemsWithBookingsAndComments() {

        Booking pastBooking = bookingRepository.save(Booking.builder()
                .item(item1)
                .booker(booker)
                .startTime(LocalDateTime.now().minusDays(2))
                .endTime(LocalDateTime.now().minusDays(1))
                .bookingStatus(ru.practicum.shareit.enums.BookingStatus.APPROVED)
                .build());

        Comment comment = commentRepository.save(Comment.builder()
                .item(item1)
                .author(booker)
                .text("Great hammer!")
                .created(LocalDateTime.now())
                .build());


        List<ItemWithBookingsDto> result = (List<ItemWithBookingsDto>) itemService.getUserItems(owner.getId());


        assertNotNull(result);
        assertEquals(2, result.size());
        ItemWithBookingsDto item1Dto = result.stream()
                .filter(dto -> dto.getId().equals(item1.getId()))
                .findFirst()
                .orElse(null);
        assertNotNull(item1Dto);

        assertNotNull(item1Dto.getBookings());
        assertFalse(item1Dto.getBookings().isEmpty());

        BookingShortDto foundPastBooking = item1Dto.getBookings().stream()
                .filter(b -> b.getId().equals(pastBooking.getId()))
                .findFirst()
                .orElse(null);
        assertNotNull(foundPastBooking);

        assertNotNull(item1Dto.getComments());
        assertEquals(1, item1Dto.getComments().size());
        assertEquals("Great hammer!", item1Dto.getComments().get(0).getText());


        ItemWithBookingsDto item2Dto = result.stream()
                .filter(dto -> dto.getId().equals(item2.getId()))
                .findFirst()
                .orElse(null);
        assertNotNull(item2Dto);
        assertNotNull(item2Dto.getBookings());
        assertTrue(item2Dto.getBookings().isEmpty());
        assertNotNull(item2Dto.getComments());
        assertTrue(item2Dto.getComments().isEmpty());
    }

    @Test
    void getUserItems_shouldThrowNotFoundException_whenUserNotFound() {

        assertThrows(NotFoundException.class, () -> {
            itemService.getUserItems(999L);
        });
    }

    @Test
    void searchItemsByText_shouldReturnMatchingItems() {

        List<ItemDto> result = itemService.searchItemsByText("hammer");


        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item1.getId(), result.get(0).getId());
        assertEquals("Hammer", result.get(0).getName());

        assertTrue(result.get(0).getAvailable());
    }

    @Test
    void searchItemsByText_shouldReturnEmptyList_whenTextIsBlank() {

        List<ItemDto> result1 = itemService.searchItemsByText("");
        List<ItemDto> result2 = itemService.searchItemsByText("   ");
        List<ItemDto> result3 = itemService.searchItemsByText(null);


        assertNotNull(result1);
        assertTrue(result1.isEmpty());
        assertNotNull(result2);
        assertTrue(result2.isEmpty());
        assertNotNull(result3);
        assertTrue(result3.isEmpty());
    }

    @Test
    void searchItemsByText_shouldReturnEmptyList_whenNoMatches() {

        List<ItemDto> result = itemService.searchItemsByText("nonexistent");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void addComment_shouldAddCommentSuccessfully() {
        Booking pastBooking = bookingRepository.save(Booking.builder()
                .item(item1)
                .booker(booker)
                .startTime(LocalDateTime.now().minusDays(2))
                .endTime(LocalDateTime.now().minusDays(1))
                .bookingStatus(ru.practicum.shareit.enums.BookingStatus.APPROVED)
                .build());

        NewCommentRequest commentRequest = new NewCommentRequest();
        commentRequest.setText("Excellent tool!");

        CommentDto result = itemService.addComment(booker.getId(), item1.getId(), commentRequest);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Excellent tool!", result.getText());
        assertEquals(booker.getName(), result.getAuthorName());
        assertNotNull(result.getCreated());

        List<Comment> comments = commentRepository.findByItem_Id(item1.getId());
        assertEquals(1, comments.size());
        assertEquals("Excellent tool!", comments.get(0).getText());
        assertEquals(booker.getId(), comments.get(0).getAuthor().getId());
    }

    @Test
    void addComment_shouldThrowValidationException_whenUserHasNoCompletedBookings() {
        NewCommentRequest commentRequest = new NewCommentRequest();
        commentRequest.setText("Great item!");

        assertThrows(ValidationException.class, () -> {
            itemService.addComment(booker.getId(), item1.getId(), commentRequest);
        });
    }

    @Test
    void addComment_shouldThrowNotFoundException_whenUserNotFound() {
        NewCommentRequest commentRequest = new NewCommentRequest();
        commentRequest.setText("Great item!");

        assertThrows(NotFoundException.class, () -> {
            itemService.addComment(999L, item1.getId(), commentRequest);
        });
    }

    @Test
    void addComment_shouldThrowNotFoundException_whenItemNotFound() {
        NewCommentRequest commentRequest = new NewCommentRequest();
        commentRequest.setText("Great item!");

        assertThrows(NotFoundException.class, () -> {
            itemService.addComment(booker.getId(), 999L, commentRequest);
        });
    }
}