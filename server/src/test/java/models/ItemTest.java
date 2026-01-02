package models;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemTest {
    @Test
    void testItemProperties() {
        Request request = new Request();
        request.setId(1L);

        Item item = new Item(10L, "Test item", "This is a test item", true);
        item.setOwnerId(20L);
        item.setRequest(request);

        assertEquals(10L, item.getId());
        assertEquals("Test item", item.getName());
        assertEquals("This is a test item", item.getDescription());
        assertEquals(20L, item.getOwnerId());
        assertEquals(request, item.getRequest());
    }
}
