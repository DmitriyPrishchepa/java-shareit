package ru.practicum.items.validation;

import ru.practicum.exception.exceptions.MissingParameterException;
import ru.practicum.items.dto.ItemDto;

public class ItemValidator {
    public static void validateItemFields(ItemDto item) {

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
}
