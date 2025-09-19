package ru.practicum.shareit.item.comments.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.item.comments.model.Comment;

import java.util.List;


public interface CommentRepository extends JpaRepository<Comment, Long>,
        QuerydslPredicateExecutor<Comment> {


    List<Comment> findByItemIdIn(List<Long> itemIds);

    List<Comment> findByItemId(Long itemId);

    List<Comment> findByAuthorId(Long authorId);


    List<Comment> findByItem_Id(Long itemId);
}
