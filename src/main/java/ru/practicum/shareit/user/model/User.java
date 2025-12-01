package ru.practicum.shareit.user.model;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.Map;

@Data
public class User {
    private Long id;
    private String name;
    private String email;
    private final Map<Long, Item> userItems = new HashMap<>();
}
