package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoToReturn;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;

@Data
public class CommentDtoToReturn {
    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
    private Booking booking;
}
