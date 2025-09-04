package ru.practicum.shareit.item.comments.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.item.comments.model.Comment;

import java.util.List;

/**
 * Репозиторий для работы с комментариями к предметам
 * Предоставляет методы для выполнения CRUD операций и пользовательских запросов к комментариям
 */
public interface CommentRepository extends JpaRepository<Comment, Long>,
        QuerydslPredicateExecutor<Comment> {

    /**
     * Находит все комментарии для списка идентификаторов предметов
     *
     * @param itemIds список идентификаторов предметов
     * @return список комментариев для указанных предметов
     */
    List<Comment> findByItemIdIn(List<Long> itemIds);

    /**
     * Находит все комментарии для указанного идентификатора предмета
     *
     * @param itemId идентификатор предмета
     * @return список комментариев для указанного предмета
     */
    List<Comment> findByItemId(Long itemId);

    /**
     * Находит все комментарии, созданные указанным автором
     *
     * @param authorId идентификатор автора комментария
     * @return список комментариев указанного автора
     */
    List<Comment> findByAuthorId(Long authorId);

    /**
     * Находит все комментарии для указанного идентификатора предмета
     * Альтернативный метод поиска по идентификатору предмета
     *
     * @param itemId идентификатор предмета
     * @return список комментариев для указанного предмета
     */
    List<Comment> findByItem_Id(Long itemId);
}
