package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JacksonTester<NewUserRequest> newUserRequestJsonTester;

    @Autowired
    private JacksonTester<UpdateUserRequest> updateUserRequestJsonTester;

    @Test
    void newUserRequest_shouldSerializeCorrectly() throws Exception {
        NewUserRequest dto = new NewUserRequest();
        dto.setName("John Doe");
        dto.setEmail("john.doe@example.com");

        JsonContent<NewUserRequest> jsonContent = newUserRequestJsonTester.write(dto);

        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("John Doe");
        assertThat(jsonContent).extractingJsonPathStringValue("$.email").isEqualTo("john.doe@example.com");

        assertThat(jsonContent).hasJsonPathStringValue("$.name");
        assertThat(jsonContent).hasJsonPathStringValue("$.email");
    }

    @Test
    void newUserRequest_shouldDeserializeCorrectly() throws Exception {
        String json = """
                {
                  "name": "Jane Smith",
                  "email": "jane.smith@example.com"
                }
                """;

        NewUserRequest dto = newUserRequestJsonTester.parseObject(json);

        assertThat(dto.getName()).isEqualTo("Jane Smith");
        assertThat(dto.getEmail()).isEqualTo("jane.smith@example.com");
    }

    @Test
    void newUserRequest_shouldDeserializeWithMissingFields_throwsException() throws IOException {
        String jsonWithMissingName = """
                {
                  "email": "test@example.com"
                }
                """;

        String jsonWithMissingEmail = """
                {
                  "name": "Test User"
                }
                """;

        NewUserRequest dto1 = newUserRequestJsonTester.parseObject(jsonWithMissingName);
        assertThat(dto1.getName()).isNull();
        assertThat(dto1.getEmail()).isEqualTo("test@example.com");

        NewUserRequest dto2 = newUserRequestJsonTester.parseObject(jsonWithMissingEmail);
        assertThat(dto2.getName()).isEqualTo("Test User");
        assertThat(dto2.getEmail()).isNull();
    }

    @Test
    void updateUserRequest_shouldSerializeCorrectly() throws Exception {
        UpdateUserRequest dto = new UpdateUserRequest();
        dto.setName("Alice Johnson");
        dto.setEmail("alice.johnson@example.com");

        JsonContent<UpdateUserRequest> jsonContent = updateUserRequestJsonTester.write(dto);

        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("Alice Johnson");
        assertThat(jsonContent).extractingJsonPathStringValue("$.email").isEqualTo("alice.johnson@example.com");
        assertThat(jsonContent).hasJsonPathStringValue("$.name");
        assertThat(jsonContent).hasJsonPathStringValue("$.email");
    }

    @Test
    void updateUserRequest_shouldDeserializeCorrectly() throws Exception {
        String json = """
                {
                  "name": "Bob Brown",
                  "email": "bob.brown@example.com"
                }
                """;

        UpdateUserRequest dto = updateUserRequestJsonTester.parseObject(json);

        assertThat(dto.getName()).isEqualTo("Bob Brown");
        assertThat(dto.getEmail()).isEqualTo("bob.brown@example.com");
    }

    @Test
    void updateUserRequest_shouldDeserializeWithNullFields() throws Exception {
        String jsonWithNullName = """
                {
                  "name": null,
                  "email": "test@example.com"
                }
                """;

        String jsonWithNullEmail = """
                {
                  "name": "Test User",
                  "email": null
                }
                """;

        String jsonWithMissingFields = """
                {
                }
                """;

        UpdateUserRequest dto1 = updateUserRequestJsonTester.parseObject(jsonWithNullName);
        assertThat(dto1.getName()).isNull();
        assertThat(dto1.getEmail()).isEqualTo("test@example.com");

        UpdateUserRequest dto2 = updateUserRequestJsonTester.parseObject(jsonWithNullEmail);
        assertThat(dto2.getName()).isEqualTo("Test User");
        assertThat(dto2.getEmail()).isNull();

        UpdateUserRequest dto3 = updateUserRequestJsonTester.parseObject(jsonWithMissingFields);
        assertThat(dto3.getName()).isNull();
        assertThat(dto3.getEmail()).isNull();
    }

    @Test
    void updateUserRequest_hasName_shouldReturnTrue_whenNameIsPresentAndNotBlank() {
        UpdateUserRequest dto = new UpdateUserRequest();
        dto.setName("Alice");

        boolean result = dto.hasName();

        assertThat(result).isTrue();
    }

    @Test
    void updateUserRequest_hasName_shouldReturnFalse_whenNameIsNull() {
        UpdateUserRequest dto = new UpdateUserRequest();
        dto.setName(null);

        boolean result = dto.hasName();

        assertThat(result).isFalse();
    }

    @Test
    void updateUserRequest_hasName_shouldReturnFalse_whenNameIsBlank() {
        UpdateUserRequest dto = new UpdateUserRequest();
        dto.setName(""); // Пустая строка
        UpdateUserRequest dto2 = new UpdateUserRequest();
        dto2.setName("   "); // Строка из пробелов

        assertThat(dto.hasName()).isFalse();
        assertThat(dto2.hasName()).isFalse();
    }

    @Test
    void updateUserRequest_hasEmail_shouldReturnTrue_whenEmailIsPresentAndNotBlank() {
        UpdateUserRequest dto = new UpdateUserRequest();
        dto.setEmail("test@example.com");

        boolean result = dto.hasEmail();

        assertThat(result).isTrue();
    }

    @Test
    void updateUserRequest_hasEmail_shouldReturnFalse_whenEmailIsNull() {
        UpdateUserRequest dto = new UpdateUserRequest();
        dto.setEmail(null);

        boolean result = dto.hasEmail();

        assertThat(result).isFalse();
    }

    @Test
    void updateUserRequest_hasEmail_shouldReturnFalse_whenEmailIsBlank() {
        UpdateUserRequest dto = new UpdateUserRequest();
        dto.setEmail("");
        UpdateUserRequest dto2 = new UpdateUserRequest();
        dto2.setEmail("   ");

        assertThat(dto.hasEmail()).isFalse();
        assertThat(dto2.hasEmail()).isFalse();
    }
}