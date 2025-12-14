package services_tests;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.util.BookingState;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoToReturn;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ItemServiceTest {

    @Mock
    ItemService itemService;

    @Mock
    UserService userService;

    @Mock
    EntityManager entityManager;

    ItemDto itemDto;
    UserDto userDto;
    TypedQuery<User> mockUserQuery;
    TypedQuery<Item> mockItemQuery;
    User mockedUser;
    Item mockedItem;

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

        mockedUser = Mockito.mock(User.class);
        Mockito.when(mockedUser.getId()).thenReturn(1L);
        Mockito.when(mockedUser.getName()).thenReturn("User1");
        Mockito.when(mockedUser.getEmail()).thenReturn("user1@yandex.ru");

        mockedItem = Mockito.mock(Item.class);
        Mockito.when(mockedItem.getId()).thenReturn(1L);
        Mockito.when(mockedItem.getName()).thenReturn("Item1");
        Mockito.when(mockedItem.getDescription()).thenReturn("Item1Descr");
        Mockito.when(mockedItem.getOwnerId()).thenReturn(1L);

        Mockito.when(userService.createUser(Mockito.any(UserDto.class)))
                .thenReturn(userDto);

        Mockito.when(itemService.addItem(Mockito.anyLong(), Mockito.any(ItemDto.class)))
                .thenReturn(itemDto);

        //mock TypedQuery class

        mockUserQuery = Mockito.mock(TypedQuery.class);
        mockItemQuery = Mockito.mock(TypedQuery.class);

        //mock set parameter

        Mockito.when(mockUserQuery.setParameter(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(mockUserQuery);

        Mockito.when(mockItemQuery.setParameter(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(mockItemQuery);

        //mock single result

        Mockito.when(mockUserQuery.getSingleResult())
                .thenReturn(mockedUser);

        Mockito.when(mockItemQuery.getSingleResult())
                .thenReturn(mockedItem);

        //--------------------------------------

        //mock create query

        Mockito.when(entityManager.createQuery(Mockito.anyString(), Mockito.eq(User.class)))
                .thenReturn(mockUserQuery);

        Mockito.when(entityManager.createQuery(Mockito.anyString(), Mockito.eq(Item.class)))
                .thenReturn(mockItemQuery);

        //createUser

        Mockito.when(userService.createUser(Mockito.mock(UserDto.class)))
                .thenReturn(userDto);

        userService.createUser(userDto);

        //createItem

        Mockito.when(itemService.addItem(Mockito.anyLong(), Mockito.any(ItemDto.class)))
                .thenReturn(itemDto);

        itemService.addItem(itemDto.getId(), itemDto);
    }

    @Test
    void createItemTest() {
        mockItemQuery = entityManager.createQuery("select i from Item", Item.class);
        Item item = mockItemQuery.getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(mockedItem.getName()));
        assertThat(item.getDescription(), equalTo(mockedItem.getDescription()));
    }

    @Test
    void updateItemTest() {

        ItemDto newItemDto = new ItemDto();
        newItemDto.setId(1L);
        newItemDto.setName("UpdatedName");
        newItemDto.setDescription("NewDescription");
        newItemDto.setAvailable(false);

        Mockito.when(mockedItem.getId()).thenReturn(1L);
        Mockito.when(mockedItem.getName()).thenReturn(newItemDto.getName());
        Mockito.when(mockedItem.getDescription()).thenReturn(newItemDto.getDescription());
        Mockito.when(mockedItem.getAvailable()).thenReturn(newItemDto.getAvailable());

        Mockito.when(
                itemService.updateItem(
                        Mockito.anyLong(),
                        Mockito.anyLong(),
                        Mockito.eq(newItemDto))
        ).thenReturn(newItemDto);

        itemService.updateItem(mockedUser.getId(), mockedItem.getId(), newItemDto);

        mockItemQuery = entityManager.createQuery("select i from Item where i.name = :name", Item.class);
        Item item = mockItemQuery.setParameter("name", newItemDto.getName()).getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(newItemDto.getName()));
        assertThat(item.getDescription(), equalTo(newItemDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(newItemDto.getAvailable()));
    }

    @Test
    void searchItemTest() {
        List<ItemDto> expectedItems = List.of(itemDto);

        Mockito.when(itemService.searchItem(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(expectedItems);

        List<ItemDto> foundItems = itemService.searchItem(mockedUser.getId(), "pdatedN");

        assertThat(foundItems, equalTo(expectedItems));
    }

    @Test
    void createCommentTest() {

        CommentDto commentDto = new CommentDto();
        commentDto.setText("comment to item");
        commentDto.setAuthor(mockedUser);
        commentDto.setItem(mockedItem);
        commentDto.setCreated(LocalDateTime.now());

        CommentDtoToReturn commentDtoToReturn = new CommentDtoToReturn();
        commentDtoToReturn.setText(commentDto.getText());
        commentDtoToReturn.setAuthorName(commentDto.getAuthor().getName());
        commentDtoToReturn.setCreated(commentDto.getCreated());
        commentDtoToReturn.setId(1L);

        Mockito.when(itemService.addComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(commentDtoToReturn);

        CommentDtoToReturn result = itemService.addComment(mockedUser.getId(), mockedItem.getId(), commentDto.getText());

        assertThat(result.getText(), equalTo(commentDto.getText()));
        assertThat(result.getAuthorName(), equalTo(commentDto.getAuthor().getName()));
    }

    @Test
    void getAllItemsFromUser() {

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(mockedItem.getId());
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(null);
        bookingDto.setStatus(BookingState.WAITING);

        BookingDto bookingDto2 = new BookingDto();
        bookingDto.setItemId(mockedItem.getId());
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));
        bookingDto.setStatus(BookingState.CANCELED);

        ItemDtoWithBookings item = new ItemDtoWithBookings();
        item.setId(mockedItem.getId());
        item.setName(mockedItem.getName());
        item.setAvailable(mockedItem.getAvailable());
        item.setDescription(mockedItem.getDescription());
        item.setBookings(List.of(bookingDto, bookingDto2));

        Mockito.when(itemService.getAllItemsFromUser(Mockito.anyLong()))
                .thenReturn(List.of(item));

        List<ItemDtoWithBookings> items = itemService.getAllItemsFromUser(mockedUser.getId());

        assertThat(items.getFirst().getName(), equalTo(mockedItem.getName()));
        assertThat(items.getFirst().getDescription(), equalTo(mockedItem.getDescription()));
        assertThat(items.getFirst().getBookings(), containsInAnyOrder(bookingDto, bookingDto2));
    }
}
