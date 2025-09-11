package ru.practicum.shareit.item.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * Репозиторий для работы с предметами.
 * Предоставляет методы для доступа к данным предметов в базе данных.
 */
public interface ItemRepository extends JpaRepository<Item, Long>,
        QuerydslPredicateExecutor<Item> {

    /**
     * Поиск доступных предметов по тексту в названии или описании.
     * Поиск осуществляется без учета регистра.
     *
     * @param text текст для поиска в названии или описании предмета
     * @return список доступных предметов, соответствующих критериям поиска
     */
    @Query("SELECT i FROM Item i WHERE " +
            "(LOWER(i.name) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', ?1, '%'))) AND " +
            "i.available = true")
    List<Item> searchAvailableItemsByText(String text);

    /**
     * Находит все предметы, принадлежащие указанному владельцу.
     *
     * @param ownerId идентификатор владельца предметов
     * @return список предметов, принадлежащих указанному владельцу
     */
    List<Item> findAllByOwner_Id(Long ownerId);

    List<Item> findByRequestRequesterId(Long requestRequesterId);

    List<Item> findByRequest_Id(Long requestId);
}
