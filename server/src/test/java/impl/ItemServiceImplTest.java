package impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.util.BookingState;
import ru.practicum.shareit.exception.exceptions.ElementNotFoundException;
import ru.practicum.shareit.exception.exceptions.MissingParameterException;
import ru.practicum.shareit.exception.exceptions.WrongUserException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.CommentDtoToReturn;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.dto.ItemWithCommentsToReturn;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ItemServiceImplTest {

    @Mock
    ItemRepository itemRepository;

    @Mock
    ItemMapper itemMapper;

    @Mock
    UserService userService;

    @Mock
    CommentMapper commentMapper;

    @Mock
    CommentRepository commentRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    BookingMapper bookingMapper;

    @InjectMocks
    ItemServiceImpl itemServiceImpl;

    @Mock
    EntityManager entityManager;

    ItemDto itemDto;
    UserDto userDto;
    BookingDto bookingDto;
    CommentDtoToReturn commentDto;
    TypedQuery<User> mockUserQuery;
    TypedQuery<Item> mockItemQuery;
    User mockedUser;
    Item mockedItem;
    Booking mockedBooking;

    @BeforeEach
    void setUp() {

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("User");
        userDto.setEmail("user@mail.ru");

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("ItemDtoName");
        itemDto.setDescription("ItemDtoDescription");
        itemDto.setOwnerId(1L);
        itemDto.setAvailable(true);

        commentDto = new CommentDtoToReturn();
        commentDto.setId(1L);
        commentDto.setText("comment");
        commentDto.setAuthorName("Jack");
        commentDto.setCreated(LocalDateTime.of(2025, 10, 10, 10, 10, 10));

        mockedUser = Mockito.mock(User.class);
        when(mockedUser.getId()).thenReturn(1L);
        when(mockedUser.getName()).thenReturn("User1");
        when(mockedUser.getEmail()).thenReturn("user1@yandex.ru");

        mockedItem = Mockito.mock(Item.class);
        when(mockedItem.getId()).thenReturn(itemDto.getId());
        when(mockedItem.getName()).thenReturn(itemDto.getName());
        when(mockedItem.getDescription()).thenReturn(itemDto.getDescription());
        when(mockedItem.getOwnerId()).thenReturn(itemDto.getOwnerId());

        //mock TypedQuery class

        mockUserQuery = Mockito.mock(TypedQuery.class);
        mockItemQuery = Mockito.mock(TypedQuery.class);

        //mock set parameter

        when(mockUserQuery.setParameter(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(mockUserQuery);

        when(mockItemQuery.setParameter(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(mockItemQuery);

        //mock single result

        when(mockUserQuery.getSingleResult())
                .thenReturn(mockedUser);

        when(mockItemQuery.getSingleResult())
                .thenReturn(mockedItem);

        //--------------------------------------

        //mock create query

        when(entityManager.createQuery(Mockito.anyString(), Mockito.eq(User.class)))
                .thenReturn(mockUserQuery);

        when(entityManager.createQuery(Mockito.anyString(), Mockito.eq(Item.class)))
                .thenReturn(mockItemQuery);

        //---------------------------------------

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
    }

    @Test
    void addItemTest() {

        when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);

        when(itemMapper.mapFromDto(Mockito.any(ItemDto.class)))
                .thenReturn(mockedItem);

        when(mockedItem.getOwnerId())
                .thenReturn(1L);

        when(itemRepository.save(Mockito.eq(mockedItem)))
                .thenReturn(mockedItem);

        when(itemMapper.mapToDto(Mockito.any(Item.class)))
                .thenReturn(itemDto);

        ItemDto item = itemServiceImpl.addItem(userDto.getId(), itemDto);

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo("ItemDtoName"));
        assertThat(item.getDescription(), equalTo("ItemDtoDescription"));
    }


    @Test
    void addItemTest_MissingParameter() {
        try {
            itemServiceImpl.addItem(null, itemDto);
        } catch (MissingParameterException e) {
            assertEquals("X-Sharer-User-Id header required", e.getMessage());
        }
    }

    @Test
    void addItemTest_UserNotFound() {
        when(userRepository.existsById(Mockito.anyLong())).thenReturn(false);

        try {
            itemServiceImpl.addItem(1L, itemDto);
        } catch (ElementNotFoundException e) {
            assertEquals("User not found", e.getMessage());
        }
    }

    @Test
    void updateItem_Success() {

        ItemDto newItemDto = new ItemDto();
        newItemDto.setId(1L);
        newItemDto.setName("newItemDto2");
        newItemDto.setDescription("newItemDto2Description");
        newItemDto.setAvailable(true);
        newItemDto.setOwnerId(mockedUser.getId());
        newItemDto.setRequestId(null);

        Item itemFromNewDto = new Item();
        itemFromNewDto.setId(newItemDto.getId());
        itemFromNewDto.setName(newItemDto.getName());
        itemFromNewDto.setDescription(newItemDto.getDescription());
        itemFromNewDto.setOwnerId(newItemDto.getOwnerId());
        itemFromNewDto.setAvailable(newItemDto.getAvailable());

        when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        when(itemRepository.existsById(Mockito.anyLong())).thenReturn(true);

        when(itemRepository.getReferenceById(Mockito.anyLong())).thenReturn(mockedItem);

        when(itemMapper.mapFromDto(Mockito.eq(newItemDto)))
                .thenReturn(itemFromNewDto);

        Item newItem = new Item();
        newItem.setId(mockedItem.getId());
        newItem.setName(mockedItem.getName());
        newItem.setDescription(mockedItem.getDescription());
        newItem.setAvailable(mockedItem.getAvailable());
        newItem.setOwnerId(mockedItem.getOwnerId());

        when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(newItem);

        when(itemMapper.mapToDto(Mockito.any(Item.class)))
                .thenReturn(newItemDto);

        ItemDto item = itemServiceImpl.updateItem(1L, 1L, newItemDto);

        assertEquals(item.getName(), newItemDto.getName());
    }

    @Test
    void updateItem_MissingParameter() {
        try {
            itemServiceImpl.updateItem(null, 1L, itemDto);
        } catch (MissingParameterException e) {
            assertEquals("userId was missing", e.getMessage());
        }
    }

    @Test
    void updateItem_UserNotFound() {

        when(userRepository.existsById(Mockito.anyLong())).thenReturn(false);

        try {
            itemServiceImpl.updateItem(1L, 1L, itemDto);
        } catch (ElementNotFoundException e) {
            assertEquals("User not found", e.getMessage());
        }
    }

    @Test
    void getItemById_Success() {
        Mockito.when(itemRepository.getReferenceById(Mockito.anyLong())).thenReturn(mockedItem);

        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment());
        Mockito.when(
                commentRepository.findAllByItemAndUserId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(comments);

        ItemWithCommentsToReturn resultDto = itemServiceImpl.getItemById(1L, 1L);

        assertEquals(1L, resultDto.getId());
        assertEquals(mockedItem.getName(), resultDto.getName());
        assertEquals(mockedItem.getDescription(), resultDto.getDescription());
        assertEquals(1L, resultDto.getOwnerId());
        assertNotNull(resultDto.getComments());
    }

    public Item createItemFromDto(
            Long id,
            String name,
            String description,
            boolean available,
            Long ownerId
    ) {
        Item newItem = new Item();
        newItem.setId(id);
        newItem.setName(name);
        newItem.setDescription(description);
        newItem.setAvailable(available);
        newItem.setOwnerId(ownerId);
        return newItem;
    }

    public BookingDto createDtoFromBooking(
            Item item,
            LocalDateTime start,
            LocalDateTime end
    ) {
        BookingDto dto = new BookingDto();
        dto.setItemId(item.getId());
        dto.setStart(start);
        dto.setEnd(end);
        return dto;
    }

    @Test
    void getAllItemsFromUserTest() {

        Long userId = 1L;

        ItemDto itemDto1 = new ItemDto();
        itemDto1.setId(1L);
        itemDto1.setName("Item 1");
        itemDto1.setDescription("Description of Item 1");
        itemDto1.setAvailable(true);
        itemDto1.setOwnerId(1L);

        ItemDto itemDto2 = new ItemDto();
        itemDto2.setId(2L);
        itemDto2.setName("Item 2");
        itemDto2.setDescription("Description of Item 2");
        itemDto2.setAvailable(false);
        itemDto2.setOwnerId(2L);

        Item item1 = createItemFromDto(
                itemDto1.getId(),
                itemDto1.getName(),
                itemDto1.getDescription(),
                itemDto1.getAvailable(),
                itemDto1.getOwnerId()
        );
        Item item2 = createItemFromDto(
                itemDto2.getId(),
                itemDto2.getName(),
                itemDto2.getDescription(),
                itemDto2.getAvailable(),
                itemDto2.getOwnerId()
        );

        Booking booking1 = new Booking();
        booking1.setItem(item1);
        booking1.setStart(LocalDateTime.now());
        booking1.setEnd(LocalDateTime.now().plusDays(7));

        Booking booking2 = new Booking();
        booking2.setItem(item2);
        booking2.setStart(LocalDateTime.now());
        booking2.setEnd(LocalDateTime.now().plusDays(14));

        BookingDto bookingDto1 = createDtoFromBooking(
                booking1.getItem(),
                booking1.getStart(),
                booking1.getEnd()
        );

        List<Item> itemsFromUser = List.of(item1, item2);

        PageRequest pageRequest = PageRequest.of(0, Integer.MAX_VALUE);

        Mockito.when(itemMapper.mapToDto(Mockito.any(Item.class)))
                .thenReturn(itemDto1);

        Mockito.when(itemRepository.findAllByOwnerId(Mockito.anyLong(), Mockito.eq(pageRequest)))
                .thenReturn(new PageImpl<>(itemsFromUser));

        List<Booking> allBookingsForAllItems = List.of(booking1, booking2);
        Mockito.when(bookingRepository.findAllByItemIdIn(Mockito.anyList())).thenReturn(allBookingsForAllItems);

        Mockito.when(bookingMapper.mapToDto(Mockito.any(Booking.class)))
                .thenReturn(bookingDto1);

        List<ItemDtoWithBookings> result = itemServiceImpl.getAllItemsFromUser(userId);

        assertEquals(2, result.size());
        assertEquals("Item 1", result.getFirst().getName());
        assertEquals("Description of Item 1", result.getFirst().getDescription());
        assertNotNull(result.getFirst().getBookings());
    }

    @Test
    void searchItemTest() {
        Long userId = 1L;
        String searchText = "searchTerm";

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Item with searchTerm");
        itemDto.setDescription("This is a test item");
        itemDto.setAvailable(true);

        Mockito.when(itemMapper.mapToDto(Mockito.any(Item.class))).thenReturn(itemDto);

        Item mockedItem = new Item();
        mockedItem.setId(1L);
        mockedItem.setName("Item with searchTerm");
        mockedItem.setDescription("This is a test item");
        mockedItem.setAvailable(true);

        Mockito.when(itemRepository.findItemsByNameContaining(Mockito.anyString()))
                .thenReturn(List.of(mockedItem));

        List<ItemDto> result = itemServiceImpl.searchItem(userId, searchText);

        assertEquals(1, result.size());
        assertEquals("Item with searchTerm", result.getFirst().getName());
        assertTrue(result.getFirst().getAvailable());
    }

    @Test
    void addCommentTest_WrongUserException_Test() {
        Long userId = 1L;
        Long itemId = 1L;
        String text = "This is a comment";

        User user = new User();
        user.setId(userId);

        Item item = new Item();
        item.setId(itemId);

        Booking booking = new Booking();
        booking.setItem(new Item());
        booking.setBooker(new User());

        Mockito.when(userRepository.getReferenceById(userId)).thenReturn(user);
        Mockito.when(itemRepository.getReferenceById(itemId)).thenReturn(item);
        Mockito.when(bookingRepository.findByItemId(itemId)).thenReturn(booking);

        try {
            itemServiceImpl.addComment(userId, itemId, text);
        } catch (WrongUserException e) {
            assertEquals("You cannot leave a comment", e.getMessage());
        }
    }

//    @Test
//    void addComment_Success() {
//        Long userId = 1L;
//        Long itemId = 1L;
//        String text = "This is a comment";
//
//        User user = new User();
//        user.setId(userId);
//
//        Item item = new Item();
//        item.setId(itemId);
//
//        Booking booking = new Booking();
//        booking.setItem(item);
//        booking.setBooker(user);
//        booking.setStart(bookingDto.getStart());
//        booking.setEnd(bookingDto.getEnd());
//        booking.setStatus(BookingState.CANCELED);
//
//        CommentDtoToReturn commentDtoToReturn = new CommentDtoToReturn();
//        commentDtoToReturn.setId(1L);
//        commentDtoToReturn.setText("");
//        commentDtoToReturn.setAuthorName(user.getName());
//        commentDtoToReturn.setBooking(booking);
//
//        Mockito.when(userRepository.getReferenceById(userId)).thenReturn(user);
//        Mockito.when(itemRepository.getReferenceById(itemId)).thenReturn(item);
//        Mockito.when(bookingRepository.findByItemId(itemId)).thenReturn(booking);
//
//        itemServiceImpl.addComment(userId, itemId, text);
//
//        verify(commentRepository, times(1)).save(Mockito.any(Comment.class));
//    }
}