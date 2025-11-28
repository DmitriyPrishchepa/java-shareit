package ru.practicum.shareit.booking.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.util.BookingState;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    public BookingDto mapToDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();

        bookingDto.setItemId(booking.getItem().getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStatus(booking.getStatus());

        return bookingDto;
    }

    public Booking mapFromDto(BookingDto bookingDto) {
        Booking booking = new Booking();

        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setStatus(BookingState.WAITING);

        return booking;
    }

    public List<BookingDto> mapToDto(List<Booking> bookings) {
        return bookings.stream()
                .map(this::mapToDto)
                .toList();
    }
}
