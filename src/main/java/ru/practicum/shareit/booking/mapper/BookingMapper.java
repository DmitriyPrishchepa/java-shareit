package ru.practicum.shareit.booking.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

@Component
public class BookingMapper {

    private final ModelMapper modelMapper = new ModelMapper();

    public BookingDto mapToDto(Booking booking) {
        return this.modelMapper.map(booking, BookingDto.class);
    }

    public Booking mapFromDto(BookingDto bookingDto) {
        return this.modelMapper.map(bookingDto, Booking.class);
    }
}
