package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс приложения ShareIt.
 * Запускает Spring Boot приложение.
 */
@SpringBootApplication
public class ShareItApp {
    /**
     * Приватный конструктор для утилитного класса.
     * Spring Boot требует публичный конструктор по умолчанию,
     * поэтому оставляем его, но Checkstyle требует приватный.
     * Компромисс - protected конструктор.
     */
    protected ShareItApp() {
        // Конструктор для Spring Boot
    }

    /**
     * Главный метод приложения.
     *
     * @param args аргументы командной строки
     */
    public static void main(final String[] args) {
        SpringApplication.run(ShareItApp.class, args);
    }

}
