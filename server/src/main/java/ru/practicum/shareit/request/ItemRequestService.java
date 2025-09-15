package ru.practicum.shareit.request;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dal.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithReplaysDto;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.dto.ReplyDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final ItemRequestMapper mapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public ItemRequestDto addRequest(Long userId, NewRequest request) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found"));

        ItemRequest itemRequest = new ItemRequest(
                request.getDescription(),
                user
        );

        return mapper.toDto(requestRepository.save(itemRequest));
    }

    public List<ItemRequestWithReplaysDto> getUserRequestsWithReplays(Long userId) {
        //Находим запросы, созданные пользователем
        List<ItemRequest> userRequests = requestRepository.findByRequesterIdOrderByCreatedDesc(userId);

        //Находим запросы, на которые пользователь отвечал предметами
        List<ItemRequest> repliedRequests = requestRepository.findByItemsOwnerId(userId);

        //Объединяем и убираем дубликаты
        Set<ItemRequest> allRequests = new HashSet<>();
        allRequests.addAll(userRequests);
        allRequests.addAll(repliedRequests);

        //Сортируем от новых к старым
        List<ItemRequest> sortedRequests = allRequests.stream()
                .sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                .collect(Collectors.toList());

        //Добавляем ответы для каждого запроса
        return sortedRequests.stream()
                .map(request -> {
                    List<Item> replyItems = itemRepository.findByRequest_Id(request.getId());
                    List<ReplyDto> replyDtos = replyItems.stream()
                            .map(mapper::toReplyDto)
                            .collect(Collectors.toList());

                    return mapper.toItemRequestWithRepliesDto(request, replyDtos);
                })
                .collect(Collectors.toList());
    }

    public List<ItemRequestDto> getAllRequests() {
        List<ItemRequest> requests = requestRepository.findAllByOrderByCreatedDesc();
        return requests.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public ItemRequestWithReplaysDto getRequestById(Long requestId) {
        ItemRequest itemRequest = requestRepository
                .findById(requestId).orElseThrow(() -> new NotFoundException("Request not found"));

        List<Item> itemsByRequest = itemRepository
                .findByRequest_Id(itemRequest.getId());

        List<ReplyDto> replyDtos = itemsByRequest.stream()
                .map(mapper::toReplyDto)
                .collect(Collectors.toList());

        return mapper.toItemRequestWithRepliesDto(itemRequest, replyDtos);
    }

}
