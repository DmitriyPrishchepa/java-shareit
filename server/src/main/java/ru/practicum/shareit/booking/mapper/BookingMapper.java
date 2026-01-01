package ru.practicum.shareit.booking.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoToReturn;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

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

        Item item = itemRepository.getReferenceById(bookingDto.getItemId());

        booking.setItem(item);
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());

        return booking;
    }

    public BookingDtoToReturn mapToBookingDtoReturned(Booking booking) {
        BookingDtoToReturn dto = new BookingDtoToReturn();
        dto.setId(booking.getId());
        dto.setItem(booking.getItem());
        dto.setBooker(booking.getBooker());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());
        return dto;
    }

    public List<BookingDtoToReturn> mapToBookingDtoReturned(List<Booking> bookings) {
        return bookings.stream()
                .map(this::mapToBookingDtoReturned)
                .toList();
    }

    public List<BookingDto> mapToListDto(List<Booking> bookings) {
        return bookings.stream().map(this::mapToDto).toList();
    }
}
