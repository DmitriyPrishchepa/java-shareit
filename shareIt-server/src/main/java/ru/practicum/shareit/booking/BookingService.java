package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoToReturn;
import ru.practicum.shareit.booking.util.BookingStateSearchParams;

import java.util.List;

public interface BookingService {
    BookingDtoToReturn createBooking(Long userId, BookingDto bookingDto);

    BookingDtoToReturn updateBookingApproval(Long userId, String bookingId, Boolean approved);

    BookingDtoToReturn findBookingById(Long userId, Long bookingId);

    List<BookingDtoToReturn> findByBookerIdAndStateSorted(Long bookerId, BookingStateSearchParams state);

    List<BookingDto> findAllByBookingItemOwnerIdAndStatus(Long ownerId, BookingStateSearchParams state);

    List<BookingDtoToReturn> findAllByBookerId(Long bookerId);

    List<BookingDto> findAllByItemOwnerId(Long bookerId);
}
