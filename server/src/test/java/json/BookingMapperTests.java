package json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoToReturn;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.util.BookingState;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {BookingMapper.class})
class BookingMapperTests {

    @Autowired
    private BookingMapper bookingMapper;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private UserRepository userRepository;

    @Test
    void testMapToDto() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(new Item(2L, "Item Name", "Description", true));
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(5));
        booking.setStatus(BookingState.WAITING);
        booking.setBooker(new User(3L, "username", "user@example.com"));

        BookingDto bookingDto = bookingMapper.mapToDto(booking);

        assertEquals(booking.getItem().getId(), bookingDto.getItemId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
    }

    @Test
    void testMapFromDto() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(2L);
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusDays(5));
        bookingDto.setStatus(BookingState.WAITING);

        Item item = new Item(2L, "Item Name", "Description", true);
        Booking booking = bookingMapper.mapFromDto(bookingDto);
        booking.setItem(item);

        assertEquals(bookingDto.getItemId(), booking.getItem().getId());
        assertEquals(bookingDto.getStart(), booking.getStart());
        assertEquals(bookingDto.getEnd(), booking.getEnd());
        assertEquals(BookingState.WAITING, booking.getStatus());
    }

    @Test
    void testMapToBookingDtoReturned() {
        // Создаем объект Booking для тестирования
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(new Item(2L, "Item Name", "Description", true));
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(5));
        booking.setStatus(BookingState.WAITING);
        booking.setBooker(new User(3L, "username", "user@example.com"));

        // Преобразуем Booking в BookingDtoToReturn
        BookingDtoToReturn bookingDtoToReturn = bookingMapper.mapToBookingDtoReturned(booking);

        // Проверяем, что все поля были корректно отображены
        assertEquals(booking.getId(), bookingDtoToReturn.getId());
        assertEquals(booking.getItem(), bookingDtoToReturn.getItem());
        assertEquals(booking.getBooker(), bookingDtoToReturn.getBooker());
        assertEquals(booking.getStart(), bookingDtoToReturn.getStart());
        assertEquals(booking.getEnd(), bookingDtoToReturn.getEnd());
        assertEquals(booking.getStatus(), bookingDtoToReturn.getStatus());
    }

    @Test
    void testMapToBookingDtoReturnedList() {
        // Создаем список объектов Booking для тестирования
        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setItem(new Item(2L, "Item Name", "Description", true));
        booking1.setStart(LocalDateTime.now());
        booking1.setEnd(LocalDateTime.now().plusDays(5));
        booking1.setStatus(BookingState.WAITING);
        booking1.setBooker(new User(3L, "username", "user@example.com"));

        Booking booking2 = new Booking();
        booking2.setId(4L);
        booking2.setItem(new Item(5L, "Another Item", "Another Description", false));
        booking2.setStart(LocalDateTime.now());
        booking2.setEnd(LocalDateTime.now().plusDays(3));
        booking2.setStatus(BookingState.APPROVED);
        booking2.setBooker(new User(6L, "anotherUser", "another@example.com"));

        List<Booking> bookings = List.of(booking1, booking2);

        // Преобразуем список Booking в список BookingDtoToReturn
        List<BookingDtoToReturn> bookingDtosToReturn = bookingMapper.mapToBookingDtoReturned(bookings);

        // Проверяем, что все поля были корректно отображены
        assertEquals(bookings.size(), bookingDtosToReturn.size());
        for (int i = 0; i < bookings.size(); i++) {
            Booking booking = bookings.get(i);
            BookingDtoToReturn bookingDtoToReturn = bookingDtosToReturn.get(i);

            assertEquals(booking.getId(), bookingDtoToReturn.getId());
            assertEquals(booking.getItem(), bookingDtoToReturn.getItem());
            assertEquals(booking.getBooker(), bookingDtoToReturn.getBooker());
            assertEquals(booking.getStart(), bookingDtoToReturn.getStart());
            assertEquals(booking.getEnd(), bookingDtoToReturn.getEnd());
            assertEquals(booking.getStatus(), bookingDtoToReturn.getStatus());
        }
    }
}
