package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.util.GlobalMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

/**
 * Реализация сервиса для работы с пользователями.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    /**
     * Репозиторий пользователей.
     */
    private final UserRepository repository;

    /**
     * Добавляет нового пользователя.
     *
     * @param request данные пользователя
     * @return созданный пользователь
     */
    public UserDto addUser(final NewUserRequest request) {
        log.debug("Adding new user by request {}", request);
        User user = repository.save(User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .build());
        log.debug("User {} has been added", user);
        return GlobalMapper.toDto(user);
    }

    /**
     * Обновляет существующего пользователя.
     *
     * @param userId  ID пользователя
     * @param request данные для обновления
     * @return обновленный пользователь
     */
    public UserDto updateUser(final Long userId,
                              final UpdateUserRequest request) {
        log.debug("Updating userId {}", userId);
        log.debug("Updating user by request {}", request);
        User user = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (request.hasEmail() && !user.getEmail().equals(request.getEmail())) {
            if (repository.existsByEmail(request.getEmail())) {
                throw new DuplicateKeyException("Email already exists");
            }
        }

        updateUserFields(user, request);

        log.debug("User {} has been updated", user);
        return GlobalMapper.toDto(user);
    }

    /**
     * Получает пользователя по ID.
     *
     * @param userId ID пользователя
     * @return найденный пользователь
     */
    public UserDto getUser(final Long userId) {
        log.debug("Getting user by userId {}", userId);
        User user = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    /**
     * Удаляет пользователя по ID.
     *
     * @param userId ID пользователя
     */
    public void deleteUser(final Long userId) {
        log.debug("Deleting user by userId {}", userId);
        if (!repository.existsById(userId)) {
            repository.deleteById(userId);
        }
        repository.deleteById(userId);
        log.debug("User {} has been deleted", userId);
    }

    private void updateUserFields(User user, UpdateUserRequest request) {
        if (request.hasName()) {
            user.setName(request.getName());
        }
        if (request.hasEmail()) {
            user.setEmail(request.getEmail());
        }
    }
}
