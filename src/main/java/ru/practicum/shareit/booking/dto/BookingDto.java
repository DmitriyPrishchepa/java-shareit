package ru.practicum.shareit.booking.dto;

import lombok.Data;
import org.springframework.context.annotation.Primary;
import ru.practicum.shareit.booking.util.BookingState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingState status;
}
