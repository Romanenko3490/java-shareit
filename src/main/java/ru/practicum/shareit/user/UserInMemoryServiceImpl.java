package ru.practicum.shareit.user;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.storage.InMemoryStorage;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;

@Service
public class UserInMemoryServiceImpl implements UserService {
    private static long USER_ID_COUNTER = 0;
    private final InMemoryStorage inMemoryStorage;

    @Autowired
    public UserInMemoryServiceImpl(InMemoryStorage inMemoryStorage) {
        this.inMemoryStorage = inMemoryStorage;
    }

    @Override
    public UserDto addUser(NewUserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        long counter = inMemoryStorage.getUsers().values().stream()
                .map(User::getEmail)
                .filter(email -> email.equals(user.getEmail()))
                .count();

        if (counter > 0) {
            throw new DuplicateKeyException("User with email " + user.getEmail() + " already exists");
        }

        USER_ID_COUNTER++;
        user.setId(USER_ID_COUNTER);
        inMemoryStorage.getUsers().put(user.getId(), user);
        inMemoryStorage.getUsersItems().put(user.getId(), new ArrayList<>());
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto updateUser(Long userId, UpdateUserRequest request) {
        if (!inMemoryStorage.getUsers().containsKey(userId)) {
            throw new NotFoundException("User with id " + userId + " not found");
        }

        if (request.getEmail() != null) {
            long counter = inMemoryStorage.getUsers().values().stream()
                    .map(User::getEmail)
                    .filter(email -> email.equals(request.getEmail()))
                    .count();

            if (counter > 0) {
                throw new DuplicateKeyException("User with email " + request.getEmail() + " already exists");
            }
        }

        User exitsUser = inMemoryStorage.getUsers().get(userId);
        User updatedUser = UserMapper.updateUser(exitsUser, request);
        inMemoryStorage.getUsers().remove(userId);
        inMemoryStorage.getUsers().put(userId, updatedUser);
        return UserMapper.mapToUserDto(updatedUser);
    }

    @Override
    public UserDto getUser(Long userId) {
        if (!inMemoryStorage.getUsers().containsKey(userId)) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
        return UserMapper.mapToUserDto(inMemoryStorage.getUsers().get(userId));
    }

    @Override
    public void deleteUser(Long userId) {
        if (!inMemoryStorage.getUsers().containsKey(userId)) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
        inMemoryStorage.getUsers().remove(userId);
        inMemoryStorage.getUsersItems().remove(userId);
    }

}
