package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.dto.comments.NewCommentRequest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JacksonTester<NewItemRequest> newItemRequestJsonTester;

    @Autowired
    private JacksonTester<UpdateItemRequest> updateItemRequestJsonTester;

    @Autowired
    private JacksonTester<NewCommentRequest> newCommentRequestJsonTester;

    @Test
    void newItemRequest_shouldSerializeCorrectly() throws Exception {
        NewItemRequest dto = new NewItemRequest();
        dto.setName("Hammer");
        dto.setDescription("A strong hammer");
        dto.setAvailable(true);
        dto.setRequestId(1L);

        JsonContent<NewItemRequest> jsonContent = newItemRequestJsonTester.write(dto);

        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("Hammer");
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo("A strong hammer");
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);

        assertThat(jsonContent).hasJsonPathStringValue("$.name");
        assertThat(jsonContent).hasJsonPathStringValue("$.description");
        assertThat(jsonContent).hasJsonPathBooleanValue("$.available");
        assertThat(jsonContent).hasJsonPathNumberValue("$.requestId");
    }

    @Test
    void newItemRequest_shouldDeserializeCorrectly() throws Exception {
        String json = """
                {
                  "name": "Screwdriver",
                  "description": "A precision screwdriver",
                  "available": false,
                  "requestId": 2
                }
                """;

        NewItemRequest dto = newItemRequestJsonTester.parseObject(json);

        assertThat(dto.getName()).isEqualTo("Screwdriver");
        assertThat(dto.getDescription()).isEqualTo("A precision screwdriver");
        assertThat(dto.getAvailable()).isEqualTo(false);
        assertThat(dto.getRequestId()).isEqualTo(2L);
    }

    @Test
    void newItemRequest_shouldDeserializeWithNullFields() throws Exception {
        String jsonWithNulls = """
                {
                  "name": null,
                  "description": null,
                  "available": null,
                  "requestId": null
                }
                """;

        NewItemRequest dto = newItemRequestJsonTester.parseObject(jsonWithNulls);

        assertThat(dto.getName()).isNull();
        assertThat(dto.getDescription()).isNull();
        assertThat(dto.getAvailable()).isNull();
        assertThat(dto.getRequestId()).isNull();
    }

    @Test
    void newItemRequest_shouldDeserializeWithMissingFields() throws Exception {
        String jsonWithMissingFields = """
                {
                }
                """;

        NewItemRequest dto = newItemRequestJsonTester.parseObject(jsonWithMissingFields);

        assertThat(dto.getName()).isNull();
        assertThat(dto.getDescription()).isNull();
        assertThat(dto.getAvailable()).isNull();
        assertThat(dto.getRequestId()).isNull();
    }

    @Test
    void newItemRequest_shouldDeserializeWithEmptyStrings() throws Exception {
        String jsonWithEmptyStrings = """
                {
                  "name": "",
                  "description": "",
                  "available": true,
                  "requestId": 3
                }
                """;

        NewItemRequest dto = newItemRequestJsonTester.parseObject(jsonWithEmptyStrings);

        assertThat(dto.getName()).isEqualTo("");
        assertThat(dto.getDescription()).isEqualTo("");
        assertThat(dto.getAvailable()).isEqualTo(true);
        assertThat(dto.getRequestId()).isEqualTo(3L);
    }

    @Test
    void updateItemRequest_shouldSerializeCorrectly() throws Exception {
        UpdateItemRequest dto = new UpdateItemRequest();
        dto.setName("Updated Hammer");
        dto.setDescription("An even stronger hammer");
        dto.setAvailable(false);

        JsonContent<UpdateItemRequest> jsonContent = updateItemRequestJsonTester.write(dto);

        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("Updated Hammer");
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo("An even stronger hammer");
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.available").isEqualTo(false);

        assertThat(jsonContent).hasJsonPathStringValue("$.name");
        assertThat(jsonContent).hasJsonPathStringValue("$.description");
        assertThat(jsonContent).hasJsonPathBooleanValue("$.available");
    }

    @Test
    void updateItemRequest_shouldDeserializeCorrectly() throws Exception {
        String json = """
                {
                  "name": "Updated Screwdriver",
                  "description": "An even more precise screwdriver",
                  "available": true
                }
                """;

        UpdateItemRequest dto = updateItemRequestJsonTester.parseObject(json);

        assertThat(dto.getName()).isEqualTo("Updated Screwdriver");
        assertThat(dto.getDescription()).isEqualTo("An even more precise screwdriver");
        assertThat(dto.getAvailable()).isEqualTo(true);
    }

    @Test
    void updateItemRequest_shouldDeserializeWithNullFields() throws Exception {
        String jsonWithNulls = """
                {
                  "name": null,
                  "description": null,
                  "available": null
                }
                """;

        UpdateItemRequest dto = updateItemRequestJsonTester.parseObject(jsonWithNulls);

        assertThat(dto.getName()).isNull();
        assertThat(dto.getDescription()).isNull();
        assertThat(dto.getAvailable()).isNull();
    }

    @Test
    void updateItemRequest_shouldDeserializeWithMissingFields() throws Exception {
        String jsonWithMissingFields = """
                {
                }
                """;

        UpdateItemRequest dto = updateItemRequestJsonTester.parseObject(jsonWithMissingFields);

        assertThat(dto.getName()).isNull();
        assertThat(dto.getDescription()).isNull();
        assertThat(dto.getAvailable()).isNull();
    }

    @Test
    void updateItemRequest_hasName_shouldReturnTrue_whenNameIsPresentAndNotBlank() {
        UpdateItemRequest dto = new UpdateItemRequest();
        dto.setName("Alice");

        boolean result = dto.hasName();

        assertThat(result).isTrue();
    }

    @Test
    void updateItemRequest_hasName_shouldReturnFalse_whenNameIsNull() {
        UpdateItemRequest dto = new UpdateItemRequest();
        dto.setName(null);

        boolean result = dto.hasName();

        assertThat(result).isFalse();
    }

    @Test
    void updateItemRequest_hasName_shouldReturnFalse_whenNameIsBlank() {
        UpdateItemRequest dto = new UpdateItemRequest();
        dto.setName(""); // Пустая строка
        UpdateItemRequest dto2 = new UpdateItemRequest();
        dto2.setName("   "); // Строка из пробелов

        assertThat(dto.hasName()).isFalse();
        assertThat(dto2.hasName()).isFalse();
    }

    @Test
    void updateItemRequest_hasDescription_shouldReturnTrue_whenDescriptionIsPresentAndNotBlank() {
        UpdateItemRequest dto = new UpdateItemRequest();
        dto.setDescription("A good item");

        boolean result = dto.hasDescription();

        assertThat(result).isTrue();
    }

    @Test
    void updateItemRequest_hasDescription_shouldReturnFalse_whenDescriptionIsNull() {
        UpdateItemRequest dto = new UpdateItemRequest();
        dto.setDescription(null);

        boolean result = dto.hasDescription();

        assertThat(result).isFalse();
    }

    @Test
    void updateItemRequest_hasDescription_shouldReturnFalse_whenDescriptionIsBlank() {
        UpdateItemRequest dto = new UpdateItemRequest();
        dto.setDescription(""); // Пустая строка
        UpdateItemRequest dto2 = new UpdateItemRequest();
        dto2.setDescription("   "); // Строка из пробелов

        assertThat(dto.hasDescription()).isFalse();
        assertThat(dto2.hasDescription()).isFalse();
    }

    @Test
    void updateItemRequest_hasAvailable_shouldReturnTrue_whenAvailableIsNotNull() {
        UpdateItemRequest dto1 = new UpdateItemRequest();
        dto1.setAvailable(true);
        UpdateItemRequest dto2 = new UpdateItemRequest();
        dto2.setAvailable(false);

        assertThat(dto1.hasAvailable()).isTrue();
        assertThat(dto2.hasAvailable()).isTrue();
    }

    @Test
    void updateItemRequest_hasAvailable_shouldReturnFalse_whenAvailableIsNull() {
        UpdateItemRequest dto = new UpdateItemRequest();
        dto.setAvailable(null);

        boolean result = dto.hasAvailable();

        assertThat(result).isFalse();
    }

    @Test
    void newCommentRequest_shouldSerializeCorrectly() throws Exception {
        NewCommentRequest dto = new NewCommentRequest();
        dto.setText("Great item!");

        JsonContent<NewCommentRequest> jsonContent = newCommentRequestJsonTester.write(dto);

        assertThat(jsonContent).extractingJsonPathStringValue("$.text").isEqualTo("Great item!");
        assertThat(jsonContent).hasJsonPathStringValue("$.text");
    }

    @Test
    void newCommentRequest_shouldDeserializeCorrectly() throws Exception {
        String json = """
                {
                  "text": "Excellent tool!"
                }
                """;

        NewCommentRequest dto = newCommentRequestJsonTester.parseObject(json);

        assertThat(dto.getText()).isEqualTo("Excellent tool!");
    }

    @Test
    void newCommentRequest_shouldDeserializeWithNullText() throws Exception {
        String jsonWithNullText = """
                {
                  "text": null
                }
                """;

        NewCommentRequest dto = newCommentRequestJsonTester.parseObject(jsonWithNullText);

        assertThat(dto.getText()).isNull();
    }

    @Test
    void newCommentRequest_shouldDeserializeWithMissingTextField() throws Exception {
        String jsonWithMissingText = """
                {
                }
                """;

        NewCommentRequest dto = newCommentRequestJsonTester.parseObject(jsonWithMissingText);

        assertThat(dto.getText()).isNull();
    }

    @Test
    void newCommentRequest_shouldDeserializeWithEmptyStringText() throws Exception {
        String jsonWithEmptyText = """
                {
                  "text": ""
                }
                """;

        NewCommentRequest dto = newCommentRequestJsonTester.parseObject(jsonWithEmptyText);

        assertThat(dto.getText()).isEqualTo("");
    }
}