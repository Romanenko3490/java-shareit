package ru.practicum.shareit.item.comments;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.comments.dto.CommentDto;
import ru.practicum.shareit.item.comments.model.Comment;

import java.util.List;

/**
 * Маппер для преобразования между сущностью Comment и DTO.
 * Обеспечивает преобразование комментариев и их списков.
 *
 * <p>Поддерживает преобразование:</p>
 * <ul>
 *   <li>Comment → CommentDto (полное преобразование комментария)</li>
 *   <li>List<Comment> → List<CommentDto> (пакетное преобразование комментариев)</li>
 * </ul>
 */
@Mapper(componentModel = "spring")
public interface CommentMapper {

    /**
     * Экземпляр маппера для использования в статическом контексте.
     */
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    /**
     * Преобразует сущность Comment в DTO представление.
     * Включает все основные поля комментария и информацию об авторе.
     *
     * @param comment сущность комментария
     * @return DTO комментария с полной информацией
     *
     * @mapping source = "id" target = "id" - маппинг идентификатора комментария
     * @mapping source = "text" target = "text" - маппинг текста комментария
     * @mapping source = "author.name" target = "authorName" - маппинг имени автора из связанной сущности User
     * @mapping source = "created" target = "created" - маппинг даты создания комментария
     */
    @Mapping(source = "id", target = "id")
    @Mapping(source = "text", target = "text")
    @Mapping(source = "author.name", target = "authorName")
    @Mapping(source = "created", target = "created")
    CommentDto toDto(Comment comment);

    /**
     * Преобразует список сущностей Comment в список DTO.
     * Автоматически использует метод toDto для каждого элемента списка.
     *
     * @param comments список сущностей комментариев
     * @return список DTO комментариев
     *
     * @see #toDto(Comment)
     */
    List<CommentDto> toDtos(List<Comment> comments);
}
