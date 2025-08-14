package ru.practicum.shareit.user;


import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

    UserDto addUser(NewUserRequest request);

    UserDto updateUser(Long userId, UpdateUserRequest request);

    UserDto getUser(Long userId);

    void deleteUser(Long userId);

}
