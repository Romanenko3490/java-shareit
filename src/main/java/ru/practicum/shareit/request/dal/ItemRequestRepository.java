package ru.practicum.shareit.request.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.request.model.ItemRequest;

/**
 * Репозиторий для работы с запросами.
 */
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long>,
        QuerydslPredicateExecutor<ItemRequest> {

}
