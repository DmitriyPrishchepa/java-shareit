package ru.practicum.shareit.booking.util;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.exceptions.BookingValidationException;

public class BookingValidator {
    public static void validateBooking(BookingDto bookingDto) {

        if (bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new BookingValidationException("Booking start date cannot be equals booking end date");
        }
    }
}
