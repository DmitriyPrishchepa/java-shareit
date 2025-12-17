package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Response;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RequestDto {
    private long id;
    private String description;
    private LocalDateTime created;
    private Long ownerId;
    private List<Response> responses;
    private List<Item> items;
}
