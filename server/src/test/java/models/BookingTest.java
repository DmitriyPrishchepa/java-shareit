package models;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.util.BookingState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingTest {

    @Test
    void testBookingProperties() {
        Item item = new Item();
        User booker = new User();

        LocalDateTime dateTimeStart = LocalDateTime.of(2020, 12, 12, 12, 12, 12);
        LocalDateTime dateTimeEnd = LocalDateTime.of(2020, 12, 12, 13, 13, 13);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(dateTimeStart);
        booking.setEnd(dateTimeEnd);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingState.APPROVED);

        assertEquals(1L, booking.getId());
        assertEquals(dateTimeStart, booking.getStart());
        assertEquals(dateTimeEnd, booking.getEnd());
        assertEquals(item, booking.getItem());
        assertEquals(booker, booking.getBooker());
        assertEquals(BookingState.APPROVED, booking.getStatus());
    }
}