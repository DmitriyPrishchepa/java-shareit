package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Item {
    private long id;
    private long ownerId;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private String category;
    private String state; //состояние товара
    private String available; //статус товара
    private final List<Review> reviews = new ArrayList<>();
}
