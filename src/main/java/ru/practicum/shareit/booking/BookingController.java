package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для работы с бронированиями.
 * Обрабатывает HTTP-запросы, связанные с бронированием предметов.
 */
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
}
