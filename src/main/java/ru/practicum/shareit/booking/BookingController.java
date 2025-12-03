package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoToReturn;
import ru.practicum.shareit.booking.util.BookingStateSearchParams;
import ru.practicum.shareit.exception.exceptions.MissingParameterException;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoToReturn createBooking(
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody BookingDto bookingDto
    ) {
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoToReturn updateBookingApproval(
            @Positive @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable(value = "bookingId", required = false) String bookingId,
            @RequestParam(value = "approved") Boolean approved) {
        if (bookingId == null || bookingId.isEmpty()) {
            throw new MissingParameterException("booking id is missing");
        }
        long id;
        try {
            id = Long.parseLong(bookingId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid booking id: " + bookingId);
        }
        return bookingService.updateBookingApproval(userId, id, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoToReturn getBookingById(
            @Positive @RequestHeader("X-Sharer-User-Id") long userId,
            @Positive @PathVariable("bookingId") Long bookingId
    ) {
        return bookingService.findBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoToReturn> getBookingsByState(
            @Positive @RequestHeader("X-Sharer-User-Id") long bookerId,
            @RequestParam(value = "state", defaultValue = "ALL") String state
    ) {
        BookingStateSearchParams bookingStateParams;

        switch (state) {
            case "CURRENT":
                bookingStateParams = BookingStateSearchParams.CURRENT;
                break;
            case "PAST":
                bookingStateParams = BookingStateSearchParams.PAST;
                break;
            case "FUTURE":
                bookingStateParams = BookingStateSearchParams.FUTURE;
                break;
            case "WAITING":
                bookingStateParams = BookingStateSearchParams.WAITING;
                break;
            default:
                return bookingService.findAllByBookerId(bookerId);
        }

        return bookingService.findByBookerIdAndStateSorted(bookerId, bookingStateParams);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookings(
            @Positive @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestParam(value = "state") String state
    ) {
        BookingStateSearchParams bookingStateParams;

        switch (state) {
            case "CURRENT":
                bookingStateParams = BookingStateSearchParams.CURRENT;
                break;
            case "PAST":
                bookingStateParams = BookingStateSearchParams.PAST;
                break;
            case "FUTURE":
                bookingStateParams = BookingStateSearchParams.FUTURE;
                break;
            case "WAITING":
                bookingStateParams = BookingStateSearchParams.WAITING;
                break;
            default:
                return bookingService.findAllByItemOwnerId(ownerId);
        }

        return bookingService.findAllByBookingItemOwnerIdAndStatus(ownerId, bookingStateParams);
    }
}
