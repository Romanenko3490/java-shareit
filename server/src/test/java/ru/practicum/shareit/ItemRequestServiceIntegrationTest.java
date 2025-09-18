package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dal.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithReplaysDto;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.dto.ReplyDto;
import ru.practicum.shareit.request.model.ItemRequest;
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
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @MockBean
    private ItemRequestMapper requestMapper;

    private ItemRequestService itemRequestService;
    private User requester;
    private User owner;
    private User anotherUser;

    @BeforeEach
    void setUp() {
        itemRequestService = new ItemRequestService(requestRepository, requestMapper, userRepository, itemRepository);

        itemRepository.deleteAll();
        requestRepository.deleteAll();
        userRepository.deleteAll();

        requester = userRepository.save(User.builder()
                .name("Requester")
                .email("requester@email.com")
                .build());

        owner = userRepository.save(User.builder()
                .name("Owner")
                .email("owner@email.com")
                .build());

        anotherUser = userRepository.save(User.builder()
                .name("Another User")
                .email("another@email.com")
                .build());

        when(requestMapper.toDto(any(ItemRequest.class))).thenAnswer(invocation -> {
            ItemRequest request = invocation.getArgument(0);
            ItemRequestDto dto = new ItemRequestDto();
            dto.setId(request.getId());
            dto.setDescription(request.getDescription());
            dto.setCreated(request.getCreated());
            return dto;
        });

        when(requestMapper.toReplyDto(any(Item.class))).thenAnswer(invocation -> {
            Item item = invocation.getArgument(0);
            ReplyDto dto = new ReplyDto();
            dto.setItemId(item.getId());
            dto.setName(item.getName());
            dto.setOwnerId(item.getOwner().getId());
            return dto;
        });

        when(requestMapper.toItemRequestWithRepliesDto(any(ItemRequest.class), any(List.class)))
                .thenAnswer(invocation -> {
                    ItemRequest request = invocation.getArgument(0);
                    List<ReplyDto> replies = invocation.getArgument(1);
                    ItemRequestWithReplaysDto dto = new ItemRequestWithReplaysDto();
                    dto.setId(request.getId());
                    dto.setDescription(request.getDescription());
                    dto.setCreated(request.getCreated());
                    dto.setItems(replies);
                    return dto;
                });
    }

    @Test
    void addRequest_shouldCreateRequestSuccessfully() {
        NewRequest newRequest = new NewRequest();
        newRequest.setDescription("Need a drill");

        ItemRequestDto result = itemRequestService.addRequest(requester.getId(), newRequest);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Need a drill", result.getDescription());
        assertNotNull(result.getCreated());

        assertTrue(requestRepository.existsById(result.getId()));
    }

    @Test
    void addRequest_shouldThrowExceptionWhenUserNotFound() {
        NewRequest newRequest = new NewRequest();
        newRequest.setDescription("Need a drill");

        assertThrows(NotFoundException.class, () -> {
            itemRequestService.addRequest(999L, newRequest);
        });
    }

    @Test
    void getRequestById_shouldReturnRequestWithReplies() {
        ItemRequest request = requestRepository.save(ItemRequest.builder()
                .description("Need a drill")
                .requester(requester)
                .created(LocalDateTime.now())
                .build());

        Item replyItem = itemRepository.save(Item.builder()
                .name("Power Drill")
                .description("Professional power drill")
                .available(true)
                .owner(owner)
                .request(request)
                .build());

        ItemRequestWithReplaysDto result = itemRequestService.getRequestById(request.getId());

        assertNotNull(result);
        assertEquals(request.getId(), result.getId());
        assertEquals("Need a drill", result.getDescription());
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());
        assertEquals(replyItem.getId(), result.getItems().get(0).getItemId());
    }

    @Test
    void getRequestById_shouldThrowExceptionWhenRequestNotFound() {
        assertThrows(NotFoundException.class, () -> {
            itemRequestService.getRequestById(999L);
        });
    }

    @Test
    void getUserRequestsWithReplays_shouldReturnEmptyListWhenNoRequests() {
        List<ItemRequestWithReplaysDto> result = itemRequestService.getUserRequestsWithReplays(requester.getId());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getUserRequestsWithReplays_shouldReturnCreatedRequests() {
        ItemRequest request1 = requestRepository.save(ItemRequest.builder()
                .description("Need a drill")
                .requester(requester)
                .created(LocalDateTime.now().minusDays(1))
                .build());

        ItemRequest request2 = requestRepository.save(ItemRequest.builder()
                .description("Need a hammer")
                .requester(requester)
                .created(LocalDateTime.now())
                .build());

        List<ItemRequestWithReplaysDto> result = itemRequestService.getUserRequestsWithReplays(requester.getId());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Need a hammer", result.get(0).getDescription());
        assertEquals("Need a drill", result.get(1).getDescription());
    }

    @Test
    void getUserRequestsWithReplays_shouldReturnRepliedRequests() {
        ItemRequest otherRequest = requestRepository.save(ItemRequest.builder()
                .description("Need a saw")
                .requester(anotherUser)
                .created(LocalDateTime.now())
                .build());

        Item replyItem = itemRepository.save(Item.builder()
                .name("Circular Saw")
                .description("Professional circular saw")
                .available(true)
                .owner(requester)
                .request(otherRequest)
                .build());

        List<ItemRequestWithReplaysDto> result = itemRequestService.getUserRequestsWithReplays(requester.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Need a saw", result.get(0).getDescription());
        assertEquals(1, result.get(0).getItems().size());
        assertEquals(replyItem.getId(), result.get(0).getItems().get(0).getItemId());
    }

    @Test
    void getUserRequestsWithReplays_shouldCombineCreatedAndRepliedRequests() {
        ItemRequest ownRequest = requestRepository.save(ItemRequest.builder()
                .description("My own request")
                .requester(requester)
                .created(LocalDateTime.now().minusDays(1))
                .build());

        ItemRequest otherRequest = requestRepository.save(ItemRequest.builder()
                .description("Other user's request")
                .requester(anotherUser)
                .created(LocalDateTime.now())
                .build());

        Item replyItem = itemRepository.save(Item.builder()
                .name("Reply Item")
                .description("Item replying to other request")
                .available(true)
                .owner(requester)
                .request(otherRequest)
                .build());

        List<ItemRequestWithReplaysDto> result = itemRequestService.getUserRequestsWithReplays(requester.getId());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Other user's request", result.get(0).getDescription());
        assertEquals("My own request", result.get(1).getDescription());
    }

    @Test
    void getUserRequestsWithReplays_shouldRemoveDuplicates() {
        ItemRequest request = requestRepository.save(ItemRequest.builder()
                .description("Test request")
                .requester(requester)
                .created(LocalDateTime.now())
                .build());

        Item replyItem = itemRepository.save(Item.builder()
                .name("Reply to own request")
                .description("Item replying to own request")
                .available(true)
                .owner(requester)
                .request(request)
                .build());

        List<ItemRequestWithReplaysDto> result = itemRequestService.getUserRequestsWithReplays(requester.getId());

        assertNotNull(result);
        assertEquals(1, result.size()); // Should not duplicate the request
        assertEquals("Test request", result.get(0).getDescription());
        assertEquals(1, result.get(0).getItems().size()); // Should contain the reply
    }

    @Test
    void getAllRequests_shouldReturnEmptyListWhenNoRequests() {
        List<ItemRequestDto> result = itemRequestService.getAllRequests();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllRequests_shouldReturnAllRequestsSortedByDate() {
        ItemRequest request1 = requestRepository.save(ItemRequest.builder()
                .description("Old request")
                .requester(requester)
                .created(LocalDateTime.now().minusDays(2))
                .build());

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            System.out.println("Delay error");
        }

        ItemRequest request3 = requestRepository.save(ItemRequest.builder()
                .description("Middle request")
                .requester(anotherUser)
                .created(LocalDateTime.now().minusDays(1))
                .build());

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            System.out.println("Delay error");
        }

        ItemRequest request2 = requestRepository.save(ItemRequest.builder()
                .description("New request")
                .requester(owner)
                .created(LocalDateTime.now())
                .build());

        List<ItemRequest> allFromDb = requestRepository.findAllByOrderByCreatedDesc();
        System.out.println("From DB (sorted DESC):");
        for (ItemRequest req : allFromDb) {
            System.out.println(" - " + req.getDescription() + " created: " + req.getCreated());
        }

        List<ItemRequestDto> result = itemRequestService.getAllRequests();

        System.out.println("From Service:");
        for (ItemRequestDto dto : result) {
            System.out.println(" - " + dto.getDescription() + " created: " + dto.getCreated());
        }

        assertNotNull(result);
        assertEquals(3, result.size());

        assertEquals("New request", result.get(0).getDescription());
        assertEquals("Middle request", result.get(1).getDescription());
        assertEquals("Old request", result.get(2).getDescription());

        assertTrue(result.get(0).getCreated().isAfter(result.get(1).getCreated()));
        assertTrue(result.get(1).getCreated().isAfter(result.get(2).getCreated()));
    }

    @Test
    void getUserRequestsWithReplays_shouldThrowExceptionWhenUserNotFound() {
        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            itemRequestService.getUserRequestsWithReplays(999L);
        });
    }
}