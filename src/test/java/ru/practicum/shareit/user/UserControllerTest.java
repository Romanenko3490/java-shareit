package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.storage.InMemoryStorage;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
class UserControllerTest {
    @Autowired
    private UserInMemoryServiceImpl userService;

    @Autowired
    private InMemoryStorage inMemoryStorage;


    private NewUserRequest newUserRequest;
    private UpdateUserRequest updateUserRequest;


    @BeforeEach
    void setUp() {
        inMemoryStorage.getUsers().clear();
        inMemoryStorage.getUsersItems().clear();
        userService.dropIdCounter();
        this.newUserRequest = new NewUserRequest();
        newUserRequest.setEmail("test@email.com");
        newUserRequest.setName("Test Name");
        this.updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setEmail("update@email.com");
        updateUserRequest.setName("Updated Test Name");
    }

    @Test
    public void userShellAddedAndGetUser() {
        UserDto addedUserDto = userService.addUser(newUserRequest);

        assertNotNull(addedUserDto);
        assertEquals(1, inMemoryStorage.getUsers().size());
        assertEquals(1, inMemoryStorage.getUsersItems().size());

        User addedUser = inMemoryStorage.getUsers().get(addedUserDto.getId());

        assertNotNull(addedUser, "User should be added to storage");
        assertEquals(newUserRequest.getEmail(), addedUser.getEmail());
        assertEquals(newUserRequest.getName(), addedUser.getName());
        assertEquals(1, addedUser.getId());
    }

    @Test
    public void userShellUpdated() {
        UserDto addedUserDto = userService.addUser(newUserRequest);
        assertNotNull(addedUserDto);
        assertEquals(1, inMemoryStorage.getUsers().size());
        assertEquals(1, inMemoryStorage.getUsersItems().size());

        User addedUser = inMemoryStorage.getUsers().get(addedUserDto.getId());

        UserDto updatedUserDto = userService.updateUser(addedUser.getId(), updateUserRequest);
        assertNotNull(updatedUserDto);
        assertEquals(1, inMemoryStorage.getUsers().size());
        assertEquals(1, inMemoryStorage.getUsersItems().size());

        User updatedUser = inMemoryStorage.getUsers().get(updatedUserDto.getId());
        assertEquals(addedUser.getId(), updatedUser.getId());
        assertEquals(updatedUser.getName(), updatedUserDto.getName());
        assertEquals(updatedUser.getEmail(), updatedUserDto.getEmail());
    }

    @Test
    public void userShellRemoved() {
        UserDto addedUserDto = userService.addUser(newUserRequest);
        assertNotNull(addedUserDto);
        assertEquals(1, inMemoryStorage.getUsers().size());
        assertEquals(1, inMemoryStorage.getUsersItems().size());

        userService.deleteUser(addedUserDto.getId());
        assertEquals(0, inMemoryStorage.getUsers().size());
        assertEquals(0, inMemoryStorage.getUsersItems().size());
    }


}