package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * Класс, представляющий бронирование предмета.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    /**
     * Уникальный идентификатор бронирования.
     */
    private Long id;

    /**
     * Дата и время начала бронирования.
     */
    private LocalDateTime startTime;

    /**
     * Дата и время окончания бронирования.
     */
    private LocalDateTime endTime;

    /**
     * Пользователь, который осуществляет бронирование.
     */
    private User booker;
}
