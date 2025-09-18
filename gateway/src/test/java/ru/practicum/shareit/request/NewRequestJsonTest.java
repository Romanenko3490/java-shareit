package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.NewRequest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class NewRequestJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JacksonTester<NewRequest> newRequestJsonTester;

    @Test
    void newRequest_shouldSerializeCorrectly() throws Exception {
        NewRequest dto = new NewRequest();
        dto.setDescription("Need a hammer");

        JsonContent<NewRequest> jsonContent = newRequestJsonTester.write(dto);

        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo("Need a hammer");
        assertThat(jsonContent).hasJsonPathStringValue("$.description");
    }

    @Test
    void newRequest_shouldDeserializeCorrectly() throws Exception {
        String json = """
                {
                  "description": "Need a screwdriver"
                }
                """;

        NewRequest dto = newRequestJsonTester.parseObject(json);

        assertThat(dto.getDescription()).isEqualTo("Need a screwdriver");
    }

    @Test
    void newRequest_shouldDeserializeWithNullDescription() throws Exception {
        String jsonWithNullDescription = """
                {
                  "description": null
                }
                """;

        NewRequest dto = newRequestJsonTester.parseObject(jsonWithNullDescription);
        assertThat(dto.getDescription()).isNull();
    }

    @Test
    void newRequest_shouldDeserializeWithMissingDescriptionField() throws Exception {
        String jsonWithMissingDescription = """
                {
                }
                """;

        NewRequest dto = newRequestJsonTester.parseObject(jsonWithMissingDescription);
        assertThat(dto.getDescription()).isNull();
    }

    @Test
    void newRequest_shouldDeserializeWithEmptyStringDescription() throws Exception {
        String jsonWithEmptyDescription = """
                {
                  "description": ""
                }
                """;

        NewRequest dto = newRequestJsonTester.parseObject(jsonWithEmptyDescription);
        assertThat(dto.getDescription()).isEqualTo("");
    }
}