package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comments.dal.CommentRepository;
import ru.practicum.shareit.item.comments.dto.CommentDto;
import ru.practicum.shareit.item.comments.dto.NewCommentRequest;
import ru.practicum.shareit.item.comments.model.Comment;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.GlobalMapper;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для работы с предметами.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {
    /**
     * Репозитории.
     */
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    /**
     * Добавляет новый предмет.
     *
     * @param userId  ID пользователя
     * @param request данные предмета
     * @return созданный предмет
     */
    public ItemDto addItem(Long userId, NewItemRequest request) {
        log.debug("Adding new item");
        User owner = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found"));
        Item item = Item.builder()
                .name(request.getName())
                .description(request.getDescription())
                .owner(owner)
                .available(request.getAvailable())
                .build();
        Item savedItem = itemRepository.save(item);
        log.debug("Creating new item {}", savedItem);
        return GlobalMapper.toDto(savedItem);
    }

    /**
     * Обновляет существующий предмет.
     *
     * @param userId  ID пользователя
     * @param itemId  ID предмета
     * @param request данные для обновления
     * @return обновленный предмет
     */
    public ItemDto updateItem(Long userId, Long itemId,
                              UpdateItemRequest request) {
        log.debug("Updating itemId {}", itemId);
        log.debug("Request {}", request);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (userId != item.getOwner().getId()) {
            throw new NotFoundException("User with id " + userId + " don't have item with id " + itemId);
        }

        updateFields(item, request);
        Item updatedItem = itemRepository.save(item);
        log.debug("Updated item {}", item);

        return GlobalMapper.toDto(updatedItem);
    }

    /**
     * Получает предмет по ID.
     *
     * @param itemId ID предмета
     * @return найденный предмет
     */
    public ItemLastNextBookingsAndCommentsDto getItem(Long itemId) {
        log.debug("Getting item {}", itemId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        List<Comment> comments = commentRepository.findByItem_Id(itemId);

        Long ownerId = item.getOwner().getId();

        // Используем методы репозитория
        Booking lastBooking = bookingRepository.findLastBookingForItem(itemId, ownerId)
                .orElse(null);
        Booking nextBooking = bookingRepository.findNextBookingForItem(itemId, ownerId)
                .orElse(null);

        BookingShortDto lastBookingShortDto = GlobalMapper.toShortDto(lastBooking);
        BookingShortDto nextBookingShortDto = GlobalMapper.toShortDto(nextBooking);

        return GlobalMapper.toItemLastNextBookingsAndCommentsDto(item, lastBookingShortDto, nextBookingShortDto, comments);

    }

    /**
     * Получает все предметы пользователя.
     *
     * @param userId ID пользователя
     * @return коллекция предметов пользователя
     */
    public Collection<ItemWithBookingsDto> getUserItems(Long userId) {
        log.debug("Getting items with bookings by user {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }

        // Одним запросом получаем все предметы пользователя
        List<Item> userItems = itemRepository.findAllByOwner_Id(userId);

        // Одним запросом получаем все бронирования для этих предметов
        List<Long> itemIds = userItems.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        List<Booking> allBookings = bookingRepository.findByItemIdIn(itemIds);
        Map<Long, List<Booking>> bookingsByItemId = allBookings.stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));

        // Одним запросом получаем все комментарии
        Map<Long, List<Comment>> commentsByItemId = commentRepository.findByItemIdIn(itemIds)
                .stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId()));

        return userItems.stream()
                .map(item -> {
                    List<Booking> itemBookings = bookingsByItemId.getOrDefault(item.getId(), Collections.emptyList());
                    List<Comment> itemComments = commentsByItemId.getOrDefault(item.getId(), Collections.emptyList());

                    return GlobalMapper.toItemWithBookingsDto(item, itemBookings, itemComments);
                })
                .collect(Collectors.toList());
    }

    /**
     * Ищет предметы по тексту.
     *
     * @param text текст для поиска
     * @return список найденных предметов
     */
    public List<ItemDto> searchItemsByText(String text) {
        log.debug("Searching items by text {}", text);
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        String searchText = text.toLowerCase();
        log.debug("Searching items by text {}", searchText);
        return itemRepository.searchAvailableItemsByText(searchText).stream()
                .map(GlobalMapper::toDto)
                .collect(Collectors.toList());
    }


    public CommentDto addComment(Long userId, Long itemId, NewCommentRequest request) {
        log.debug("Adding new comment {}", request);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found"));
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Item not found"));

        boolean hasValidBooking = bookingRepository.hasUserCompletedBookings(userId, itemId);

        if (!hasValidBooking) {
            log.debug("User {} has no bookings for item {}", userId, itemId);
            throw new ValidationException("User must have completed bookings for item " + itemId);
        }

        Comment comment = commentRepository.save(Comment.builder()
                .text(request.getText())
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build());
        log.debug("Added comment {}", comment);

        return GlobalMapper.toCommentDto(comment);
    }

    /**
     * Вспомогательный метод.
     *
     * @param item    предемет для обновления
     * @param request запрос на обновление
     * @return список найденных предметов
     */
    private Item updateFields(final Item item,
                              final UpdateItemRequest request) {
        if (request.hasName()) {
            item.setName(request.getName());
        }
        if (request.hasDescription()) {
            item.setDescription(request.getDescription());
        }
        if (request.hasAvailable()) {
            item.setAvailable(request.getAvailable());
        }
        return item;
    }

}
