package impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoToReturn;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.util.BookingState;
import ru.practicum.shareit.booking.util.BookingStateSearchParams;
import ru.practicum.shareit.exception.exceptions.BookingValidationException;
import ru.practicum.shareit.exception.exceptions.ElementNotFoundException;
import ru.practicum.shareit.exception.exceptions.MissingParameterException;
import ru.practicum.shareit.exception.exceptions.WrongUserException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BookingServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    BookingMapper bookingMapper;

    @Mock
    UserService userService;

    @Mock
    ItemService itemService;

    @Mock
    BookingService bookingService;

    @InjectMocks
    BookingServiceImpl bookingServiceImpl;

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

    Booking checkBooking;
    BookingDtoToReturn checkBookingDtoToReturn;

    private BookingDtoToReturn mapToReturn(
            Long id,
            LocalDateTime start,
            LocalDateTime end,
            User booker,
            Item item,
            BookingState bookingState
    ) {
        BookingDtoToReturn dto = new BookingDtoToReturn();
        dto.setId(id);
        dto.setStart(start);
        dto.setEnd(end);
        dto.setBooker(booker);
        dto.setItem(item);
        dto.setStatus(bookingState);
        return dto;
    }

    @BeforeEach
    void setUp() {

        userDto = new UserDto();
        userDto.setName("user1");
        userDto.setEmail("user1@mail.ru");

        mockedUser = mock(User.class);
        Mockito.when(mockedUser.getId()).thenReturn(1L);
        Mockito.when(mockedUser.getName()).thenReturn("user1");
        Mockito.when(mockedUser.getEmail()).thenReturn("user1@mail.ru");

        mockUserQuery = mock(TypedQuery.class);

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
        itemDto.setName("item1");
        itemDto.setDescription("item1Descr");
        itemDto.setOwnerId(mockedUser.getId());
        itemDto.setAvailable(true);

        mockedItem = mock(Item.class);
        Mockito.when(mockedItem.getName()).thenReturn("item1");
        Mockito.when(mockedItem.getDescription()).thenReturn("item1Descr");
        Mockito.when(mockedItem.getOwnerId()).thenReturn(1L);
        Mockito.when(mockedItem.getAvailable()).thenReturn(true);

        mockItemQuery = mock(TypedQuery.class);

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
        bookingDto.setStart(LocalDateTime.of(2024, 11, 11, 11, 11, 11));
        bookingDto.setEnd(LocalDateTime.of(2024, 12, 12, 12, 12, 12));

        mockedBooking = mock(Booking.class);

        mockedBooking = mock(Booking.class);
        Mockito.when(mockedBooking.getId()).thenReturn(1L);
        Mockito.when(mockedBooking.getStart()).thenReturn(bookingDto.getStart());
        Mockito.when(mockedBooking.getEnd()).thenReturn(bookingDto.getEnd());
        Mockito.when(mockedBooking.getStatus()).thenReturn(bookingDto.getStatus());
        Mockito.when(mockedBooking.getItem()).thenReturn(mockedItem);
        Mockito.when(mockedBooking.getBooker()).thenReturn(mockedUser);

        mockBookingQuery = mock(TypedQuery.class);

        returnedBookingDto = new BookingDtoToReturn();
        returnedBookingDto.setId(1L);
        returnedBookingDto.setStart(bookingDto.getStart());
        returnedBookingDto.setEnd(bookingDto.getEnd());
        returnedBookingDto.setStatus(bookingDto.getStatus());
        returnedBookingDto.setItem(mockedItem);
        returnedBookingDto.setBooker(mockedUser);

        Mockito.when(bookingService.createBooking(Mockito.eq(userDto.getId()), Mockito.eq(bookingDto)))
                .thenReturn(returnedBookingDto);

        Mockito.when(mockBookingQuery.setParameter(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(mockBookingQuery);

        Mockito.when(mockBookingQuery.getSingleResult())
                .thenReturn(mockedBooking);

        Mockito.when(entityManager.createQuery(Mockito.anyString(), Mockito.eq(Booking.class)))
                .thenReturn(mockBookingQuery);

        //для проверки состояний

        checkBooking = new Booking();
        checkBooking.setStart(LocalDateTime.of(2026, 1, 1, 0, 0));
        checkBooking.setEnd(LocalDateTime.of(2027, 1, 2, 0, 0)); // Дата окончания должна быть в прошлом
        checkBooking.setItem(mockedItem);
        checkBooking.setBooker(mockedUser);
        checkBooking.setStatus(BookingState.APPROVED);

        checkBookingDtoToReturn = mapToReturn(
                checkBooking.getId(),
                checkBooking.getStart(),
                checkBooking.getEnd(),
                checkBooking.getBooker(),
                checkBooking.getItem(),
                checkBooking.getStatus()
        );

        //инструкции
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.existsById(Mockito.anyLong())).thenReturn(false);
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
    void createBookingUserNotFoundTest() {
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(false);

        try {
            bookingServiceImpl.createBooking(userDto.getId(), new BookingDto());
        } catch (ElementNotFoundException e) {
            assertEquals("User not found", e.getMessage());
        }
    }

    @Test
    void createBookingItemNotFoundTest() {

        try {
            bookingServiceImpl.createBooking(1L, bookingDto);
        } catch (ElementNotFoundException e) {
            assertEquals("Item not found", e.getMessage());
        }
    }

    @Test
    void createBookingItemNotAvailableTest() {
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.existsById(Mockito.anyLong())).thenReturn(true);

        Mockito.when(mockedItem.getAvailable())
                .thenReturn(false);

        Mockito.when(itemRepository.getReferenceById(Mockito.anyLong())).thenReturn(mockedItem);

        Mockito.when(bookingMapper.mapFromDto(Mockito.any(BookingDto.class)))
                .thenReturn(mockedBooking);

        Mockito.when(bookingRepository.save(Mockito.eq(mockedBooking)))
                .thenReturn(mockedBooking);
        try {
            bookingServiceImpl.createBooking(1L, bookingDto);
        } catch (BookingValidationException e) {
            assertEquals("Item not available", e.getMessage());
        }
    }

    @Test
    void createBookingStartDateEqualsEndDateTest() {
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(mockedUser);
        Mockito.when(itemRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.getReferenceById(Mockito.anyLong())).thenReturn(mockedItem);

        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setItemId(1L);
        bookingDto1.setStatus(BookingState.WAITING);
        bookingDto1.setStart(LocalDateTime.of(2024, 11, 11, 11, 11, 11));
        bookingDto1.setEnd(LocalDateTime.of(2024, 11, 11, 11, 11, 11));

        Mockito.when(bookingMapper.mapFromDto(Mockito.any(BookingDto.class)))
                .thenReturn(mockedBooking);

        Mockito.when(bookingRepository.save(Mockito.eq(mockedBooking)))
                .thenReturn(mockedBooking);

        try {
            bookingServiceImpl.createBooking(1L, bookingDto1);
        } catch (BookingValidationException e) {
            assertEquals("Booking start date cannot be equals booking end date", e.getMessage());
        }
    }

    @Test
    void createBookingStartDateNotEqualsEndDateTest() {
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(mockedUser);
        Mockito.when(itemRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.getReferenceById(Mockito.anyLong())).thenReturn(mockedItem);

        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setItemId(1L);
        bookingDto1.setStatus(BookingState.WAITING);
        bookingDto1.setStart(LocalDateTime.of(2024, 11, 11, 11, 11, 11));
        bookingDto1.setEnd(LocalDateTime.of(2025, 11, 11, 11, 11, 11));

        Mockito.when(bookingMapper.mapFromDto(Mockito.any(BookingDto.class)))
                .thenReturn(mockedBooking);

        Mockito.when(bookingRepository.save(Mockito.eq(mockedBooking)))
                .thenReturn(mockedBooking);

        try {
            BookingDtoToReturn returnedDto = bookingServiceImpl.createBooking(1L, bookingDto1);
            assertNotNull(returnedDto);
            assertEquals(mockedBooking.getId(), returnedDto.getId());
            assertEquals(mockedBooking.getItem(), returnedDto.getItem());
            assertEquals(mockedBooking.getBooker(), returnedDto.getBooker());
            assertEquals(mockedBooking.getStart(), returnedDto.getStart());
            assertEquals(mockedBooking.getEnd(), returnedDto.getEnd());
            assertEquals(BookingState.WAITING, returnedDto.getStatus());
        } catch (BookingValidationException e) {
            assertEquals("Booking start date cannot be equals booking end date", e.getMessage());
        }
    }

    @Test
    void updateBookingApprovalTest_ValidationException_isMissing() {
        try {
            bookingServiceImpl.updateBookingApproval(1L, null, true);
        } catch (MissingParameterException e) {
            assertEquals("booking id is missing", e.getMessage());
        }
    }

    @Test
    void updateBookingApprovalTest_ValidationException_IllegalArgument() {
        try {
            bookingServiceImpl.updateBookingApproval(1L, "abc1", true);
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid booking id: " + "abc1", e.getMessage());
        }
    }

    @Test
    void updateBookingApproval_WrongUser() {

        Mockito.when(bookingServiceImpl.checkExitstingBooking(Mockito.anyLong()))
                .thenReturn(mockedBooking);

        Mockito.when(mockedBooking.getItem().getOwnerId())
                .thenReturn(3L);

        try {
            bookingServiceImpl.updateBookingApproval(1L, "1", true);
        } catch (WrongUserException e) {
            assertEquals("You are not the owner of the item and cannot update the booking.", e.getMessage());
        }

        Mockito.when(mockedBooking.getItem().getOwnerId())
                .thenReturn(1L);
    }

    @Test
    void updateBookingApprovalTest_Approved() {
        Mockito.when(mockedBooking.getStatus())
                .thenReturn(BookingState.APPROVED);

        Mockito.when(bookingServiceImpl.checkExitstingBooking(Mockito.eq(mockedBooking.getId())))
                .thenReturn(mockedBooking);

        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(mockedBooking);

        Mockito.when(bookingMapper.mapToBookingDtoReturned(Mockito.any(Booking.class)))
                .thenReturn(returnedBookingDto);

        BookingDtoToReturn resultDto = bookingServiceImpl.updateBookingApproval(1L, "1", true);

        assertEquals(BookingState.APPROVED, resultDto.getStatus());
    }

    @Test
    void updateBookingApprovalTest_REJECTED() {
        Mockito.when(mockedBooking.getStatus())
                .thenReturn(BookingState.REJECTED);

        Mockito.when(bookingServiceImpl.checkExitstingBooking(Mockito.eq(mockedBooking.getId())))
                .thenReturn(mockedBooking);

        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(mockedBooking);

        Mockito.when(bookingMapper.mapToBookingDtoReturned(Mockito.any(Booking.class)))
                .thenReturn(returnedBookingDto);

        BookingDtoToReturn resultDto = bookingServiceImpl.updateBookingApproval(1L, "1", false);

        assertEquals(BookingState.REJECTED, resultDto.getStatus());
    }

    @Test
    void findBookingById_NotExistedBooking() {
        Mockito.when(bookingRepository.getReferenceById(Mockito.anyLong()))
                .thenThrow(EntityNotFoundException.class);

        try {
            bookingServiceImpl.findBookingById(1L, 1L);
        } catch (ElementNotFoundException e) {
            assertEquals("Booking not found", e.getMessage());
        }
    }

    @Test
    void getBookingInfo_OwnerAccess_Success() {

        Mockito.when(bookingServiceImpl.checkExitstingBooking(Mockito.anyLong())).thenReturn(mockedBooking);

        Mockito.when(bookingMapper.mapToBookingDtoReturned(Mockito.eq(mockedBooking))).thenReturn(returnedBookingDto);

        BookingDtoToReturn resultDto = bookingServiceImpl.findBookingById(1L, 1L);

        assertEquals(returnedBookingDto, resultDto);
    }

    @Test
    void getBookingInfo_BookerAccess_Success() {
        Mockito.when(bookingServiceImpl.checkExitstingBooking(Mockito.anyLong())).thenReturn(mockedBooking);

        BookingDtoToReturn expectedDto = new BookingDtoToReturn();
        Mockito.when(bookingMapper.mapToBookingDtoReturned(mockedBooking)).thenReturn(expectedDto);

        BookingDtoToReturn resultDto = bookingServiceImpl.findBookingById(1L, 1L);

        assertEquals(expectedDto, resultDto);
    }

    @Test
    void getBookingInfo_WrongUser_ThrowsWrongUserException() {

        Mockito.when(mockedBooking.getBooker().getId())
                .thenReturn(2L);

        Mockito.when(mockedBooking.getItem().getOwnerId())
                .thenReturn(3L);

        Mockito.when(bookingServiceImpl.checkExitstingBooking(Mockito.anyLong())).thenReturn(mockedBooking);

        try {
            bookingServiceImpl.findBookingById(1L, 1L);
        } catch (WrongUserException e) {
            assertEquals("Only owner or booker can get information about booking", e.getMessage());
        }

        Mockito.when(mockedBooking.getBooker().getId())
                .thenReturn(1L);

        Mockito.when(mockedBooking.getItem().getId())
                .thenReturn(1L);
    }

    @Test
    void findByBookerIdAndStateSortedPastTest() {

        Mockito.when(bookingMapper.mapToBookingDtoReturned(Mockito.eq(checkBooking)))
                .thenReturn(checkBookingDtoToReturn);

        Mockito.when(bookingMapper.mapToBookingDtoReturned(Mockito.anyList()))
                .thenReturn(List.of(checkBookingDtoToReturn));

        Mockito.when(bookingRepository.findPastBookingsByBookerId(
                        Mockito.anyLong(), Mockito.any(LocalDateTime.class)))
                .thenReturn(List.of(checkBooking));

        List<BookingDtoToReturn> result =
                bookingServiceImpl.findByBookerIdAndStateSorted(1L, BookingStateSearchParams.PAST);

        assertThat(result, Matchers.not(empty()));
    }

    @Test
    void findByBookerIdAndStateSortedFutureTest() {
        checkBooking.setStatus(BookingState.APPROVED);

        checkBookingDtoToReturn = mapToReturn(
                checkBooking.getId(),
                checkBooking.getStart(),
                checkBooking.getEnd(),
                checkBooking.getBooker(),
                checkBooking.getItem(),
                checkBooking.getStatus()
        );

        Mockito.when(bookingMapper.mapToBookingDtoReturned(Mockito.eq(checkBooking)))
                .thenReturn(checkBookingDtoToReturn);

        Mockito.when(bookingMapper.mapToBookingDtoReturned(Mockito.anyList()))
                .thenReturn(List.of(checkBookingDtoToReturn));

        Mockito.when(bookingRepository.findFutureBookingsByBookerId(
                        Mockito.anyLong(), Mockito.any(LocalDateTime.class)))
                .thenReturn(List.of(checkBooking));

        List<BookingDtoToReturn> result =
                bookingServiceImpl.findByBookerIdAndStateSorted(1L, BookingStateSearchParams.FUTURE);

        assertThat(result, Matchers.not(empty()));
    }

    @Test
    void findByBookerIdAndStateSortedCurrentTest() {
        checkBooking.setStatus(BookingState.APPROVED);

        checkBookingDtoToReturn = mapToReturn(
                checkBooking.getId(),
                checkBooking.getStart(),
                checkBooking.getEnd(),
                checkBooking.getBooker(),
                checkBooking.getItem(),
                checkBooking.getStatus()
        );

        Mockito.when(bookingMapper.mapToBookingDtoReturned(Mockito.eq(checkBooking)))
                .thenReturn(checkBookingDtoToReturn);

        Mockito.when(bookingMapper.mapToBookingDtoReturned(Mockito.anyList()))
                .thenReturn(List.of(checkBookingDtoToReturn));

        Mockito.when(bookingRepository.findCurrentBookingsByBookerId(
                        Mockito.anyLong(), Mockito.any(LocalDateTime.class)))
                .thenReturn(List.of(checkBooking));

        List<BookingDtoToReturn> result =
                bookingServiceImpl.findByBookerIdAndStateSorted(1L, BookingStateSearchParams.CURRENT);

        assertThat(result, Matchers.not(empty()));
    }

    @Test
    void findByBookerIdAndStateSortedWaitingTest() {
        checkBooking.setStatus(BookingState.WAITING);

        checkBookingDtoToReturn = mapToReturn(
                checkBooking.getId(),
                checkBooking.getStart(),
                checkBooking.getEnd(),
                checkBooking.getBooker(),
                checkBooking.getItem(),
                checkBooking.getStatus()
        );

        Mockito.when(bookingMapper.mapToBookingDtoReturned(Mockito.eq(checkBooking)))
                .thenReturn(checkBookingDtoToReturn);

        Mockito.when(bookingMapper.mapToBookingDtoReturned(Mockito.anyList()))
                .thenReturn(List.of(checkBookingDtoToReturn));

        Mockito.when(bookingRepository.findAllByBookerIdAndStatus(
                        Mockito.anyLong(), Mockito.eq(BookingStateSearchParams.WAITING)))
                .thenReturn(List.of(checkBooking));

        List<BookingDtoToReturn> result =
                bookingServiceImpl.findByBookerIdAndStateSorted(1L, BookingStateSearchParams.WAITING);

        assertThat(result, Matchers.not(empty()));
    }

    @Test
    void findByBookerIdAndStateSortedRejectedTest() {
        checkBooking.setStatus(BookingState.REJECTED);

        checkBookingDtoToReturn = mapToReturn(
                checkBooking.getId(),
                checkBooking.getStart(),
                checkBooking.getEnd(),
                checkBooking.getBooker(),
                checkBooking.getItem(),
                checkBooking.getStatus()
        );

        Mockito.when(bookingMapper.mapToBookingDtoReturned(Mockito.eq(checkBooking)))
                .thenReturn(checkBookingDtoToReturn);

        Mockito.when(bookingMapper.mapToBookingDtoReturned(Mockito.anyList()))
                .thenReturn(List.of(checkBookingDtoToReturn));

        Mockito.when(bookingRepository.findAllByBookerIdAndStatus(
                        Mockito.anyLong(), Mockito.eq(BookingStateSearchParams.REJECTED)))
                .thenReturn(List.of(checkBooking));

        List<BookingDtoToReturn> result = bookingServiceImpl.findByBookerIdAndStateSorted(1L, BookingStateSearchParams.REJECTED);

        assertThat(result, Matchers.not(empty()));
    }

    @Test
    void findByBookerIdAndStateSortedDefaultTest() {
        checkBooking.setStatus(BookingState.WAITING);

        checkBookingDtoToReturn = mapToReturn(
                checkBooking.getId(),
                checkBooking.getStart(),
                checkBooking.getEnd(),
                checkBooking.getBooker(),
                checkBooking.getItem(),
                checkBooking.getStatus()
        );

        Mockito.when(bookingMapper.mapToBookingDtoReturned(Mockito.eq(checkBooking)))
                .thenReturn(checkBookingDtoToReturn);

        Mockito.when(bookingMapper.mapToBookingDtoReturned(Mockito.anyList()))
                .thenReturn(List.of(checkBookingDtoToReturn));

        Mockito.when(bookingRepository.findAllByBookerId(Mockito.anyLong()))
                .thenReturn(List.of(checkBooking));

        List<BookingDtoToReturn> result = bookingServiceImpl.findByBookerIdAndStateSorted(1L, BookingStateSearchParams.ALL);

        assertEquals(1, result.size());
    }

    //----------------------------------------------

    @Test
    void findAllByBookingItemOwnerIdAndStatusTest_PAST() {

        Mockito.when(itemRepository.existsById(Mockito.anyLong())).thenReturn(true);

        User userForTest = new User();
        userForTest.setName("User1");
        userForTest.setId(1L);
        userForTest.setEmail("userEmail");

        Item itemForTest = new Item();
        itemForTest.setId(1L);
        itemForTest.setOwnerId(1L);
        itemForTest.setAvailable(true);
        itemForTest.setDescription("Item1");
        itemForTest.setRequest(new Request());

        Booking bookingForTest = new Booking();
        bookingForTest.setId(1L);
        bookingForTest.setItem(itemForTest);
        bookingForTest.setBooker(userForTest);
        bookingForTest.setStatus(BookingState.CANCELED);

        BookingDto dto = new BookingDto();
        dto.setItemId(bookingForTest.getItem().getId());
        dto.setStart(bookingForTest.getStart());
        dto.setEnd(bookingForTest.getEnd());
        dto.setStatus(bookingForTest.getStatus());

        bookingForTest.setStart(LocalDateTime.of(2023, 12, 12, 12, 12, 12));
        bookingForTest.setEnd(LocalDateTime.of(2024, 12, 12, 12, 12, 12));

        Mockito.when(bookingMapper.mapToDto(Mockito.eq(bookingForTest)))
                .thenReturn(dto);

        Mockito.when(bookingMapper.mapToListDto(Mockito.anyList()))
                .thenReturn(List.of(dto));

        Mockito.when(bookingRepository.findPastBookingsByOwnerId(Mockito.anyLong(), Mockito.eq(LocalDateTime.now())))
                .thenReturn(List.of(bookingForTest));

        List<BookingDto> result = bookingServiceImpl.findAllByBookingItemOwnerIdAndStatus(1L, BookingStateSearchParams.PAST);

        assertThat(result, Matchers.not(empty()));
        assertThat(result.size(), Matchers.is(1));
    }

    @Test
    void findAllByBookingItemOwnerIdAndStatusTest_WAITING() {

        User userForTest = new User();
        userForTest.setName("User1");
        userForTest.setId(1L);
        userForTest.setEmail("userEmail");

        Item itemForTest = new Item();
        itemForTest.setId(1L);
        itemForTest.setOwnerId(1L);
        itemForTest.setAvailable(false);
        itemForTest.setDescription("Item1");
        itemForTest.setRequest(new Request());

        Booking bookingForTest = new Booking();
        bookingForTest.setId(1L);
        bookingForTest.setItem(itemForTest);
        bookingForTest.setBooker(userForTest);
        bookingForTest.setStatus(BookingState.WAITING);
        bookingForTest.setStart(LocalDateTime.of(2023, 12, 12, 12, 12, 12));
        bookingForTest.setEnd(LocalDateTime.of(2027, 12, 12, 12, 12, 12));

        BookingDto dto = new BookingDto();
        dto.setItemId(itemForTest.getId());
        dto.setStart(bookingForTest.getStart());
        dto.setEnd(bookingForTest.getEnd());
        dto.setStatus(bookingForTest.getStatus());

        List<Booking> list = List.of(bookingForTest);

        Mockito.when(bookingRepository.findAllByOwnerIdAndStatus(
                        Mockito.anyLong(), Mockito.eq(BookingStateSearchParams.WAITING)))
                .thenReturn(List.of(bookingForTest));

        Mockito.when(bookingMapper.mapToListDto(list))
                .thenReturn(List.of(dto));

        List<BookingDto> result =
                bookingServiceImpl.findAllByBookingItemOwnerIdAndStatus(1L, BookingStateSearchParams.WAITING);

        assertThat(result, Matchers.not(empty()));
    }

    @Test
    void findAllByBookingItemOwnerIdAndStatusTest_REJECTED() {

        User userForTest = new User();
        userForTest.setName("User1");
        userForTest.setId(1L);
        userForTest.setEmail("userEmail");

        Item itemForTest = new Item();
        itemForTest.setId(1L);
        itemForTest.setOwnerId(1L);
        itemForTest.setAvailable(false);
        itemForTest.setDescription("Item1");
        itemForTest.setRequest(new Request());

        Booking bookingForTest = new Booking();
        bookingForTest.setId(1L);
        bookingForTest.setItem(itemForTest);
        bookingForTest.setBooker(userForTest);
        bookingForTest.setStatus(BookingState.REJECTED);
        bookingForTest.setStart(LocalDateTime.of(2023, 12, 12, 12, 12, 12));
        bookingForTest.setEnd(LocalDateTime.of(2027, 12, 12, 12, 12, 12));

        BookingDto dto = new BookingDto();
        dto.setItemId(itemForTest.getId());
        dto.setStart(bookingForTest.getStart());
        dto.setEnd(bookingForTest.getEnd());
        dto.setStatus(bookingForTest.getStatus());

        List<Booking> list = List.of(bookingForTest);

        Mockito.when(bookingRepository.findAllByOwnerIdAndStatus(
                        Mockito.anyLong(), Mockito.eq(BookingStateSearchParams.REJECTED)))
                .thenReturn(List.of(bookingForTest));

        Mockito.when(bookingMapper.mapToListDto(list))
                .thenReturn(List.of(dto));

        List<BookingDto> result =
                bookingServiceImpl.findAllByBookingItemOwnerIdAndStatus(1L, BookingStateSearchParams.REJECTED);

        assertThat(result, Matchers.not(empty()));
    }

    @Test
    void findAllByBookingItemOwnerIdAndStatusTest_DEFAULT() {

        User userForTest = new User();
        userForTest.setName("User1");
        userForTest.setId(1L);
        userForTest.setEmail("userEmail");

        Item itemForTest = new Item();
        itemForTest.setId(1L);
        itemForTest.setOwnerId(1L);
        itemForTest.setAvailable(false);
        itemForTest.setDescription("Item1");
        itemForTest.setRequest(new Request());

        Booking bookingForTest = new Booking();
        bookingForTest.setId(1L);
        bookingForTest.setItem(itemForTest);
        bookingForTest.setBooker(userForTest);
        bookingForTest.setStatus(BookingState.APPROVED);
        bookingForTest.setStart(LocalDateTime.of(2023, 12, 12, 12, 12, 12));
        bookingForTest.setEnd(LocalDateTime.of(2027, 12, 12, 12, 12, 12));

        BookingDto dto = new BookingDto();
        dto.setItemId(itemForTest.getId());
        dto.setStart(bookingForTest.getStart());
        dto.setEnd(bookingForTest.getEnd());
        dto.setStatus(bookingForTest.getStatus());

        List<Booking> list = List.of(bookingForTest);

        Mockito.when(bookingRepository.findAllByOwnerIdAndStatus(
                        Mockito.anyLong(), Mockito.eq(BookingStateSearchParams.ALL)))
                .thenReturn(List.of(bookingForTest));

        Mockito.when(bookingMapper.mapToListDto(list))
                .thenReturn(List.of(dto));

        Mockito.when(bookingRepository.findAllByItemOwnerId(
                        Mockito.anyLong()))
                .thenReturn(List.of(bookingForTest));

        List<BookingDto> result =
                bookingServiceImpl.findAllByBookingItemOwnerIdAndStatus(1L, BookingStateSearchParams.ALL);

        assertThat(result, Matchers.not(empty()));
    }
}
