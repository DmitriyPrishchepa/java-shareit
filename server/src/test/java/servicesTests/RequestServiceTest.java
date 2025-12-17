package servicesTests;

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
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestService;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.model.Response;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RequestServiceTest {

    @Mock
    RequestService requestService;

    @Mock
    UserService userService;

    @Mock
    ItemService itemService;

    @Mock
    EntityManager entityManager;

    Request request;
    UserDto userDto;
    ItemDto itemDto;
    Request mockedRequest;
    User mockedUser;
    Item mockedItem;
    TypedQuery<User> mockUserQuery;
    TypedQuery<Item> mockItemQuery;
    TypedQuery<Request> mockRequestQuery;


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

        Mockito.when(mockUserQuery.getSingleResult())
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

        request = new Request();
        request.setId(1L);
        request.setDescription("requestDescription");
        request.setOwnerId(mockedUser.getId());

        mockedRequest = Mockito.mock(Request.class);
        Mockito.when(mockedRequest.getId()).thenReturn(1L);
        Mockito.when(mockedRequest.getDescription()).thenReturn("requestDescription");
        Mockito.when(mockedRequest.getCreated()).thenReturn(LocalDateTime.now());
        Mockito.when(mockedRequest.getOwnerId()).thenReturn(1L);

        mockRequestQuery = Mockito.mock(TypedQuery.class);

        Mockito.when(requestService.createRequest(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(request);

        Mockito.when(mockRequestQuery.setParameter(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(mockRequestQuery);

        Mockito.when(mockRequestQuery.getSingleResult())
                .thenReturn(request);

        Mockito.when(entityManager.createQuery(Mockito.anyString(), Mockito.eq(Request.class)))
                .thenReturn(mockRequestQuery);

        requestService.createRequest(mockedUser.getId(), "requestDescription");
    }


    @Test
    void createRequestTest() {

        mockRequestQuery = entityManager.createQuery("select r from Request r ", Request.class);
        Request requestCreated = mockRequestQuery.getSingleResult();

        assertThat(requestCreated.getId(), notNullValue());
        assertThat(requestCreated.getId(), equalTo(1L));
        assertThat(requestCreated.getDescription(), equalTo("requestDescription"));
    }

    @Test
    void getRequestByIdTest() {

        Mockito.when(requestService.getRequestById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(request);

        Request resultRequest = requestService.getRequestById(mockedUser.getId(), mockedRequest.getId());

        assertThat(resultRequest.getDescription(), equalTo("requestDescription"));
    }

    @Test
    void getRequestsWithResponsesTest() {

        Response response1 = new Response();
        response1.setId(1L);
        response1.setName("response1");
        response1.setOwnerId(1L);
        response1.setItemId(1L);
        response1.setRequest(request);

        Response response2 = new Response();
        response1.setId(2L);
        response1.setName("response2");
        response1.setOwnerId(1L);
        response1.setItemId(1L);
        response1.setRequest(request);

        request.setResponses(List.of(response1, response2));

        Mockito.when(requestService.getUserRequestsWithResponses(Mockito.anyLong()))
                .thenReturn(List.of(request));

        List<Request> requests = requestService.getUserRequestsWithResponses(1L);

        assertThat(requests.getFirst().getResponses(), containsInAnyOrder(response1, response2));
    }

    @Test
    void getOtherUsersRequestsTest() {

        UserDto userDto2 = new UserDto();
        userDto2.setId(2L);
        userDto2.setName("user2");
        userDto2.setEmail("user2@mail.ru");

        Mockito.when(userService.createUser(Mockito.eq(userDto2)))
                .thenReturn(userDto2);

        userService.createUser(userDto2);

        ItemDto itemDto2 = new ItemDto();
        itemDto2.setId(2L);
        itemDto2.setName("item2");
        itemDto2.setDescription("item2Descr");
        itemDto2.setAvailable(true);
        itemDto2.setOwnerId(2L);

        Mockito.when(itemService.addItem(Mockito.anyLong(), Mockito.eq(itemDto2)))
                .thenReturn(itemDto2);

        itemService.addItem(2L, itemDto2);

        Request request2 = new Request();
        request2.setId(2L);
        request2.setDescription("request2Descr");
        request2.setOwnerId(2L);
        request2.setCreated(LocalDateTime.now());

        Mockito.when(requestService.createRequest(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(request2);

        requestService.createRequest(2L, "request2Descr");

        Mockito.when(requestService.getOtherUsersRequests(Mockito.anyLong()))
                .thenReturn(List.of(request2));

        List<Request> otherUsersRequests = requestService.getOtherUsersRequests(2L);

        assertThat(otherUsersRequests, notNullValue());
        assertThat(otherUsersRequests, contains(request2));
    }
}
