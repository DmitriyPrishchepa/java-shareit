package ru.practicum.shareit.item.model;

import lombok.Data;

import java.util.Date;

@Data
public class Review {
    private long id;
    private long userId;
    private long bookingId;
    private String description;
    private Date creationDate;
    private int rate;
}
