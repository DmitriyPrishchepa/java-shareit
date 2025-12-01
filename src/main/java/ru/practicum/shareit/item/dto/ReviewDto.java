package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ReviewDto {
    private long userId;
    private long bookingId;
    private String description;
    private Date creationDate;
    private int rate;
}
