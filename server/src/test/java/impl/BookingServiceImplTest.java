package impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoToReturn;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.util.BookingState;
import ru.practicum.shareit.exception.exceptions.AccessToCommentDeniedException;
import ru.practicum.shareit.exception.exceptions.WrongUserException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsToReturn;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BookingServiceImplTest {
    @Mock
    UserService userService;

    @Mock
    ItemService itemService;

    @Mock
    BookingServiceImpl bookingService;

    @Mock
    EntityManager entityManager;

    UserDto userDto;
    ItemDto itemDto;
    BookingDto bookingDto;
    User mockedUser;
    Item mockedItem;
    Booking mockedBooking;
    TypedQuery<User> mockUserQuery;
    TypedQuery<Item> mockItemQuery;
    TypedQuery<Booking> mockBookingQuery;
    BookingDtoToReturn returnedBookingDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("user1");
        userDto.setEmail("user1@mail.ru");

        mockedUser = Mockito.mock(User.class);
        Mockito.when(mockedUser.getId()).thenReturn(1L);
        Mockito.when(mockedUser.getName()).thenReturn("user1");
        Mockito.when(mockedUser.getEmail()).thenReturn("user1@mail.ru");

        mockUserQuery = Mockito.mock(TypedQuery.class);

        Mockito.when(userService.createUser(Mockito.any(UserDto.class)))
                .thenReturn(userDto);

        Mockito.when(mockUserQuery.setParameter(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(mockUserQuery);

        Mockito.when(mockUserQuery
                        .getSingleResult())
                .thenReturn(mockedUser);

        userService.createUser(userDto);

        //--------------------------------------

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("item1");
        itemDto.setDescription("item1Descr");
        itemDto.setOwnerId(mockedUser.getId());
        itemDto.setAvailable(true);

        mockedItem = Mockito.mock(Item.class);
        Mockito.when(mockedItem.getName()).thenReturn("item1");
        Mockito.when(mockedItem.getDescription()).thenReturn("item1Descr");
        Mockito.when(mockedItem.getOwnerId()).thenReturn(1L);

        mockItemQuery = Mockito.mock(TypedQuery.class);

        Mockito.when(itemService.addItem(Mockito.anyLong(), Mockito.any(ItemDto.class)))
                .thenReturn(itemDto);

        Mockito.when(mockItemQuery.setParameter(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(mockItemQuery);

        Mockito.when(mockItemQuery.getSingleResult())
                .thenReturn(mockedItem);

        itemService.addItem(mockedUser.getId(), itemDto);

        //------------------------------------------

        //request

        bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStatus(BookingState.WAITING);
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(null);

        mockedBooking = Mockito.mock(Booking.class);

        mockedBooking = Mockito.mock(Booking.class);
        Mockito.when(mockedBooking.getId()).thenReturn(1L);
        Mockito.when(mockedBooking.getStart()).thenReturn(bookingDto.getStart());
        Mockito.when(mockedBooking.getEnd()).thenReturn(bookingDto.getEnd());
        Mockito.when(mockedBooking.getStatus()).thenReturn(bookingDto.getStatus());
        Mockito.when(mockedBooking.getItem()).thenReturn(mockedItem);

        mockBookingQuery = Mockito.mock(TypedQuery.class);

        returnedBookingDto = new BookingDtoToReturn();
        returnedBookingDto.setId(1L);
        returnedBookingDto.setStart(bookingDto.getStart());
        returnedBookingDto.setEnd(bookingDto.getEnd());
        returnedBookingDto.setStatus(bookingDto.getStatus());
        returnedBookingDto.setItem(mockedItem);

        Mockito.when(bookingService.createBooking(Mockito.anyLong(), Mockito.any(BookingDto.class)))
                .thenReturn(returnedBookingDto);

        Mockito.when(mockBookingQuery.setParameter(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(mockBookingQuery);

        Mockito.when(mockBookingQuery.getSingleResult())
                .thenReturn(mockedBooking);

        Mockito.when(entityManager.createQuery(Mockito.anyString(), Mockito.eq(Booking.class)))
                .thenReturn(mockBookingQuery);

        bookingService.createBooking(1L, bookingDto);
    }

    @Test
    void createBookingTest() {

        mockBookingQuery = entityManager.createQuery("select b from Booking b", Booking.class);
        Booking booking = mockBookingQuery.getSingleResult();

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), equalTo(bookingDto.getStart()));
        assertThat(booking.getStatus(), equalTo(bookingDto.getStatus()));
    }

    @Test
    void updateBookingApprovalTest() {

        Mockito.when(bookingService.updateBookingApproval(Mockito.anyLong(), Mockito.anyString(), Mockito.anyBoolean()))
                .thenReturn(returnedBookingDto);

        returnedBookingDto = bookingService.updateBookingApproval(1L, "1L", true);

        assertThat(returnedBookingDto.getStatus(), equalTo(BookingState.WAITING));
        assertThat(returnedBookingDto.getStart(), equalTo(bookingDto.getStart()));
    }

    @Test
    void getBookingByIdTest() {

        Mockito.when(bookingService.findBookingById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(returnedBookingDto);

        returnedBookingDto = bookingService.findBookingById(1L, 1L);

        assertThat(returnedBookingDto.getId(), equalTo(1L));
        assertThat(returnedBookingDto.getItem(), equalTo(mockedItem));
    }

    @Test
    void testWrongUserException() {
        String text = "Test comment";

        ItemWithCommentsToReturn item = new ItemWithCommentsToReturn();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());

        Mockito.when(userService.getUserById(userDto.getId())).thenReturn(userDto);
        Mockito.when(itemService.getItemById(userDto.getId(), itemDto.getId())).thenReturn(item);
        Mockito.when(bookingService.findBookingById(userDto.getId(), returnedBookingDto.getId())).thenReturn(returnedBookingDto);

        Mockito.when(itemService.addComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString()))
                .thenThrow(WrongUserException.class);

        assertThrows(WrongUserException.class, () -> itemService.addComment(3L, 3L, text));
    }

    @Test
    void testAccessToCommentDeniedException() {

        String text = "Test comment";

        ItemWithCommentsToReturn item = new ItemWithCommentsToReturn();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());

        Mockito.when(userService.getUserById(userDto.getId())).thenReturn(userDto);
        Mockito.when(itemService.getItemById(userDto.getId(), itemDto.getId())).thenReturn(item);
        Mockito.when(bookingService.findBookingById(userDto.getId(), returnedBookingDto.getId())).thenReturn(returnedBookingDto);

        Mockito.when(itemService.addComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString()))
                .thenThrow(AccessToCommentDeniedException.class);

        assertThrows(AccessToCommentDeniedException.class, () -> itemService.addComment(1L, 1L, text));
    }
}
