package ru.practicum.shareit.item.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

/**
 * Репозиторий для работы с предметами.
 */
public interface ItemRepository extends JpaRepository<Item, Long> {

}
