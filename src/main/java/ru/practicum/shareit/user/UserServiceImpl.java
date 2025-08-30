package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

/**
 * Реализация сервиса для работы с пользователями.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
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
    @Override
    public UserDto addUser(final NewUserRequest request) {
        log.debug("Adding new user by request {}", request);
        User user = repository.save(UserMapper.mapToUser(request));
        log.debug("User {} has been added", user);
        return UserMapper.mapToUserDto(user);
    }

    /**
     * Обновляет существующего пользователя.
     *
     * @param userId  ID пользователя
     * @param request данные для обновления
     * @return обновленный пользователь
     */
    @Override
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

        UserMapper.updateUser(user, request);
        log.debug("User {} has been updated", user);
        return UserMapper.mapToUserDto(user);
    }

    /**
     * Получает пользователя по ID.
     *
     * @param userId ID пользователя
     * @return найденный пользователь
     */
    @Override
    public UserDto getUser(final Long userId) {
        log.debug("Getting user by userId {}", userId);
        return UserMapper.mapToUserDto(repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found")));
    }

    /**
     * Удаляет пользователя по ID.
     *
     * @param userId ID пользователя
     */
    @Override
    public void deleteUser(final Long userId) {
        log.debug("Deleting user by userId {}", userId);
        if (!repository.existsById(userId)) {
            repository.deleteById(userId);
        }
        repository.deleteById(userId);
        log.debug("User {} has been deleted", userId);
    }
}
