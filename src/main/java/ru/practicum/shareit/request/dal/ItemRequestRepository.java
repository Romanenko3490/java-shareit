package ru.practicum.shareit.request.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.QItem;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.QItemRequest;

import java.util.List;

/**
 * Репозиторий для работы с запросами.
 */
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long>,
        QuerydslPredicateExecutor<ItemRequest> {


    List<ItemRequest> findByRequesterIdOrderByCreatedDesc(Long requesterId);


    List<ItemRequest> findAllByOrderByCreatedDesc();

    @Query("SELECT DISTINCT ir FROM ItemRequest ir " +
            "JOIN Item i ON i.request.id = ir.id " +
            "WHERE i.owner.id = :userId")
    List<ItemRequest> findByItemsOwnerId(@Param("userId") Long userId);

}
