package ru.practicum.shareit.request;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для работы с запросами на предметы.
 * Обрабатывает HTTP-запросы, связанные с созданием и получением запросов.
 */
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
}
