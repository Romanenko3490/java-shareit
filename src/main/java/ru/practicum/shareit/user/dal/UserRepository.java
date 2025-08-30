package ru.practicum.shareit.user.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

/**
 * Репозиторий для работы с пользователями.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Проверяет существование пользователя с email.
     *
     * @param email email для проверки
     * @return true если пользователь с таким email существует
     */
    boolean existsByEmail(String email);
}
