package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.dto.NewUserRequest;
import ru.practicum.shareit.dto.UpdateUserRequest;
import ru.practicum.shareit.dto.UserDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private UserMapper userMapper;

    private UserService userService;
    private NewUserRequest newUserRequest;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, userMapper);
        userRepository.deleteAll();

        newUserRequest = NewUserRequest.builder()
                .name("Test User")
                .email("test@email.com")
                .build();

        when(userMapper.toDto(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return UserDto.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .build();
        });
    }

    @Test
    void addUser_shouldCreateUserSuccessfully() {

        UserDto result = userService.addUser(newUserRequest);


        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(newUserRequest.getName(), result.getName());
        assertEquals(newUserRequest.getEmail(), result.getEmail());

        User savedUser = userRepository.findById(result.getId()).orElseThrow();
        assertEquals(newUserRequest.getName(), savedUser.getName());
        assertEquals(newUserRequest.getEmail(), savedUser.getEmail());
    }

    @Test
    void addUser_shouldThrowExceptionWhenEmailAlreadyExists() {

        userService.addUser(newUserRequest);

        NewUserRequest duplicateEmailRequest = NewUserRequest.builder()
                .name("Another User")
                .email(newUserRequest.getEmail())
                .build();


        assertThrows(DataIntegrityViolationException.class, () -> {
            userService.addUser(duplicateEmailRequest);
        });
    }

    @Test
    void getUser_shouldReturnUserWhenExists() {

        UserDto createdUser = userService.addUser(newUserRequest);


        UserDto result = userService.getUser(createdUser.getId());


        assertNotNull(result);
        assertEquals(createdUser.getId(), result.getId());
        assertEquals(createdUser.getName(), result.getName());
        assertEquals(createdUser.getEmail(), result.getEmail());
    }

    @Test
    void getUser_shouldThrowNotFoundExceptionWhenUserNotExists() {
        assertThrows(NotFoundException.class, () -> {
            userService.getUser(999L);
        });
    }

    @Test
    void updateUser_shouldUpdateNameSuccessfully() {

        UserDto createdUser = userService.addUser(newUserRequest);
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setName("Updated Name");

        UserDto result = userService.updateUser(createdUser.getId(), updateRequest);

        assertNotNull(result);
        assertEquals(createdUser.getId(), result.getId());
        assertEquals("Updated Name", result.getName());
        assertEquals(createdUser.getEmail(), result.getEmail());


        User updatedUser = userRepository.findById(createdUser.getId()).orElseThrow();
        assertEquals("Updated Name", updatedUser.getName());
        assertEquals(createdUser.getEmail(), updatedUser.getEmail());
    }

    @Test
    void updateUser_shouldUpdateEmailSuccessfully() {

        UserDto createdUser = userService.addUser(newUserRequest);
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setEmail("updated@email.com");


        UserDto result = userService.updateUser(createdUser.getId(), updateRequest);


        assertNotNull(result);
        assertEquals(createdUser.getId(), result.getId());
        assertEquals(createdUser.getName(), result.getName());
        assertEquals("updated@email.com", result.getEmail());


        User updatedUser = userRepository.findById(createdUser.getId()).orElseThrow();
        assertEquals("updated@email.com", updatedUser.getEmail());
    }
}