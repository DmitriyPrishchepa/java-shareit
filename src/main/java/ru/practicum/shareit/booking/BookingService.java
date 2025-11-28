package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoToReturnValue;
import ru.practicum.shareit.booking.util.BookingStateSearchParams;

import java.util.List;

public interface BookingService {
    BookingDtoToReturnValue createBooking(Long userId, BookingDto bookingDto);

    BookingDto updateBookingApproval(Long userId, Long bookingId, Boolean approved);

    BookingDto findBookingById(Long userId, Long bookingId);

    List<BookingDto> findByBookerIdAndStateSorted(Long bookerId, BookingStateSearchParams state);

    List<BookingDto> findAllByBookingItemOwnerIdAndStatus(Long ownerId, BookingStateSearchParams state);

    List<BookingDto> findAllByBookerId(Long bookerId);

    List<BookingDto> findAllByItemOwnerId(Long bookerId);
}
