package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemWithCommentsToReturn {
    private long id;
    private Long ownerId;
    private String name;
    private String description;
    private Boolean available;
    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
    private List<Comment> comments;
}
