package ru.practicum.shareit.item.util;

import ru.practicum.shareit.exception.exceptions.MissingParameterException;
import ru.practicum.shareit.item.model.Item;

public class ItemValidator {
    public static void validateItemFields(Item item) {

        if (item.getAvailable() == null) {
            throw new MissingParameterException("Available cannot be null");
        }

        if (item.getName() == null || item.getName().isBlank()) {
            throw new MissingParameterException("Name cannot be null or blank");
        }

        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new MissingParameterException("Description cannot be null or blank");
        }
    }

    public static Item validateAndUpdateItemFields(Item existingItem, Item item) {
        if (item.getName() != null) {
            existingItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            existingItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            existingItem.setAvailable(item.getAvailable());
        }
        return existingItem;
    }
}
