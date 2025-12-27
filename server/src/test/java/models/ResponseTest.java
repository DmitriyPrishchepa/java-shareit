package models;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.model.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResponseTest {

    @Test
    void testResponseProperties() {
        Request request = new Request();
        request.setId(1L);

        Response response = new Response();
        response.setId(10L);
        response.setRequest(request);
        response.setItemId(20L);
        response.setName("Test item");
        response.setOwnerId(30L);

        assertEquals(10L, response.getId());
        assertEquals(request, response.getRequest());
        assertEquals(20L, response.getItemId());
        assertEquals("Test item", response.getName());
        assertEquals(30L, response.getOwnerId());
    }
}
