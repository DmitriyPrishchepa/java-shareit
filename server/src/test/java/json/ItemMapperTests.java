package json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {ItemMapper.class})
public class ItemMapperTests {

    @Autowired
    private ItemMapper itemMapper;

    @Test
    void testMapToDto() {
        Item item = new Item(1L, "Item Name", "Description", true);
        item.setOwnerId(2L);

        ItemDto itemDto = itemMapper.mapToDto(item);

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getOwnerId(), itemDto.getOwnerId());
    }

    @Test
    void testMapFromDto() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(3L);
        itemDto.setName("Another Item Name");
        itemDto.setDescription("Another Description");
        itemDto.setAvailable(false);
        itemDto.setOwnerId(4L);

        Item item = itemMapper.mapFromDto(itemDto);

        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getOwnerId(), item.getOwnerId());
    }
}
