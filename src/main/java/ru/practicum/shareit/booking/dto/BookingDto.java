package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.util.Date;

@Data
public class BookingDto {
    private Date startDate;
    private Date finishDate;
    private int rentalPrice;
}
