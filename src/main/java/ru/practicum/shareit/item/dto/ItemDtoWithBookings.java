package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Data
public class ItemDtoWithBookings {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private List<BookingDto> bookings;
}
