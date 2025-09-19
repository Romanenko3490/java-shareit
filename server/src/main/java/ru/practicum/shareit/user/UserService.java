package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.dto.NewUserRequest;
import ru.practicum.shareit.dto.UpdateUserRequest;
import ru.practicum.shareit.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dal.UserRepository;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public UserDto addUser(NewUserRequest request) {
        log.debug("Adding new user by request {}", request);
        User user = repository.save(User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .build());
        log.debug("User {} has been added", user);
        return mapper.toDto(user);
    }


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
        User updatedUser = repository.save(user);

        log.debug("User {} has been updated", user);
        return mapper.toDto(updatedUser);
    }


    @Transactional(readOnly = true)
    public UserDto getUser(final Long userId) {
        log.debug("Getting user by userId {}", userId);
        User user = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return mapper.toDto(user);
    }

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
