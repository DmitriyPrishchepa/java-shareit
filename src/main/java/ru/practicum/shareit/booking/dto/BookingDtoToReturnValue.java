package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.util.BookingState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class BookingDtoToReturnValue {
    private Item item;
    private User booker;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingState status;
}
