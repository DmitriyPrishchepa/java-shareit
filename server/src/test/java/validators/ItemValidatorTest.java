package validators;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.exceptions.MissingParameterException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.util.ItemValidator;

import static org.junit.jupiter.api.Assertions.*;

public class ItemValidatorTest {

    private final ItemValidator itemValidator = new ItemValidator();

    @Test
    void testValidateItemFields_Success() {
        Item item = new Item();
        item.setName("Test item");
        item.setDescription("This is a test item");
        item.setAvailable(true);

        assertDoesNotThrow(() -> itemValidator.validateItemFields(item));
    }

    @Test
    void testValidateItemFields_MissingAvailable() {
        Item item = new Item();
        item.setName("Test item");
        item.setDescription("This is a test item");

        assertThrows(MissingParameterException.class, () -> itemValidator.validateItemFields(item),
                "Available cannot be null");
    }

    @Test
    void testValidateItemFields_MissingName() {
        Item item = new Item();
        item.setDescription("This is a test item");
        item.setAvailable(true);

        assertThrows(MissingParameterException.class, () -> itemValidator.validateItemFields(item),
                "Name cannot be null or blank");
    }

    @Test
    void testValidateItemFields_MissingDescription() {
        Item item = new Item();
        item.setName("Test item");
        item.setAvailable(true);

        assertThrows(MissingParameterException.class, () -> itemValidator.validateItemFields(item),
                "Description cannot be null or blank");
    }

    @Test
    void testValidateAndUpdateItemFields() {
        Item existingItem = new Item();
        existingItem.setName("Old name");
        existingItem.setDescription("Old description");
        existingItem.setAvailable(false);

        Item item = new Item();
        item.setName("New name");
        item.setDescription(null);
        item.setAvailable(true);

        Item updatedItem = itemValidator.validateAndUpdateItemFields(existingItem, item);

        assertEquals("New name", updatedItem.getName());
        assertEquals("Old description", updatedItem.getDescription()); // не должно было обновиться
    }
}
