package ru.practicum.bookings;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.bookings.dto.BookItemRequestDto;
import ru.practicum.bookings.dto.BookingState;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @RequestHeader("X-Sharer-User-Id") @Positive Long userId,
            @RequestBody @Valid BookItemRequestDto bookingDto
    ) {
        log.info("Create booking {}", bookingDto);
        return bookingClient.bookItem(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBookingApproval(
            @RequestHeader("X-Sharer-User-Id") @Positive long userId,
            @PathVariable(value = "bookingId", required = false) String bookingId,
            @RequestParam(value = "approved") Boolean approved
    ) {
        log.info("Update approval, status is {}", approved);
        return bookingClient.updateBookingApproval(userId, bookingId, approved);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByState(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                                     @RequestParam(name = "state", defaultValue = "all") String stateParam) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}", stateParam, userId);
        return bookingClient.getBookingsByState(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsOfOwner(@RequestHeader("X-Sharer-User-Id") @Positive long ownerId,
                                                     @RequestParam(name = "state", defaultValue = "all") String stateParam) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking of owner with state {}, userId={}", stateParam, ownerId);
        return bookingClient.getAllBookingsOfOwner(ownerId, state);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                             @PathVariable @Positive Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }
}
