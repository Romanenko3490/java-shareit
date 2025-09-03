package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.UserBookingDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

/**
 * Маппер для преобразования между сущностью User и DTO представлениями.
 * Обеспечивает преобразование пользовательских данных для различных контекстов использования.
 *
 * <p>Поддерживает преобразование:</p>
 * <ul>
 *   <li>User → UserDto (полное преобразование со всеми полями)</li>
 *   <li>User → UserBookingDto (краткое преобразование для использования в бронированиях)</li>
 * </ul>
 */
@Mapper
public interface UserMapper {

    /**
     * Экземпляр маппера для использования в статическом контексте.
     * Позволяет использовать маппер без инъекции зависимостей.
     */
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    /**
     * Преобразует сущность User в полное DTO представление.
     * Включает все поля пользователя: идентификатор, имя и email.
     * MapStruct автоматически маппит поля с одинаковыми именами.
     *
     * @param user сущность пользователя
     * @return полное DTO пользователя со всей информацией
     */
    UserDto toDto(User user);

    /**
     * Преобразует сущность User в краткое DTO представление для использования в контексте бронирований.
     * Содержит только идентификатор и имя пользователя, исключая email.
     *
     * @param user сущность пользователя
     * @return краткое DTO пользователя для бронирований
     * @mapping source = "id" target = "id" - маппинг идентификатора пользователя
     * @mapping source = "name" target = "name" - маппинг имени пользователя
     * Поле email автоматически игнорируется, так как отсутствует в UserBookingDto
     */
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    UserBookingDto toUserBookingDto(User user);
}