package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.NewBookingRequest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class NewBookingRequestJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JacksonTester<NewBookingRequest> newBookingRequestJsonTester;


    @Test
    void newBookingRequest_shouldSerializeCorrectly() throws Exception {
        NewBookingRequest dto = new NewBookingRequest();
        dto.setItemId(1L);
        LocalDateTime start = LocalDateTime.of(2023, 10, 20, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 10, 20, 12, 0, 0);
        dto.setStart(start);
        dto.setEnd(end);

        JsonContent<NewBookingRequest> jsonContent = newBookingRequestJsonTester.write(dto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.start").isEqualTo("2023-10-20T10:00:00");
        assertThat(jsonContent).extractingJsonPathStringValue("$.end").isEqualTo("2023-10-20T12:00:00");

        assertThat(jsonContent).hasJsonPathNumberValue("$.itemId");
        assertThat(jsonContent).hasJsonPathStringValue("$.start");
        assertThat(jsonContent).hasJsonPathStringValue("$.end");
    }

    @Test
    void newBookingRequest_shouldDeserializeCorrectly() throws Exception {
        String json = """
                {
                  "itemId": 2,
                  "start": "2023-10-21T14:30:00",
                  "end": "2023-10-21T16:30:00"
                }
                """;

        NewBookingRequest dto = newBookingRequestJsonTester.parseObject(json);

        assertThat(dto.getItemId()).isEqualTo(2L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2023, 10, 21, 14, 30, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2023, 10, 21, 16, 30, 0));
    }

    @Test
    void newBookingRequest_shouldDeserializeWithNullFields() throws Exception {
        String jsonWithNulls = """
                {
                  "itemId": null,
                  "start": null,
                  "end": null
                }
                """;

        NewBookingRequest dto = newBookingRequestJsonTester.parseObject(jsonWithNulls);

        assertThat(dto.getItemId()).isNull();
        assertThat(dto.getStart()).isNull();
        assertThat(dto.getEnd()).isNull();
    }

    @Test
    void newBookingRequest_shouldDeserializeWithMissingFields() throws Exception {
        String jsonWithMissingFields = """
                {
                }
                """;

        NewBookingRequest dto = newBookingRequestJsonTester.parseObject(jsonWithMissingFields);

        assertThat(dto.getItemId()).isNull();
        assertThat(dto.getStart()).isNull();
        assertThat(dto.getEnd()).isNull();
    }
}