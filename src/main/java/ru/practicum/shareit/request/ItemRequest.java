package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Date;

@Data
public class ItemRequest {
    private long id;
    private long userId;
    private String description;
    private Date creationDate;
    private final ArrayList<Item> suggestedItems = new ArrayList<>();
}
