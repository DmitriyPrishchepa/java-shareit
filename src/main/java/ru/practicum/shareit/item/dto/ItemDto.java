package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemDto {
    private long id;
    private Long ownerId;
    private String name;
    private String description;
    private Boolean available;
}
