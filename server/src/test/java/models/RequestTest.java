package models;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.model.Request;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestTest {
    @Test
    void testRequestProperties() {

        LocalDateTime created = LocalDateTime.of(2020, 12, 12, 12, 12, 12);

        Request request = new Request();
        request.setId(1L);
        request.setDescription("Test description");
        request.setCreated(created);
        request.setOwnerId(10L);

        assertEquals(1L, request.getId());
        assertEquals("Test description", request.getDescription());
        assertEquals(created, request.getCreated());
        assertEquals(10L, request.getOwnerId());
    }
}
