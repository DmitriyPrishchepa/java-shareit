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
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.RequestServiceImpl;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.model.Response;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RequestServiceImplTest {
    @Mock
    RequestServiceImpl requestService;

    @Mock
    UserService userService;

    @Mock
    ItemService itemService;

    @Mock
    RequestRepository requestRepository;

    @InjectMocks
    RequestServiceImpl requestServiceImpl;

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

    Request makeRequest(
            Long id,
            String description,
            LocalDateTime created,
            Long ownerId,
            List<Response> responses,
            List<Item> items
    ) {
        Request req = new Request();
        req.setId(id);
        req.setDescription(description);
        req.setCreated(created);
        req.setItems(items);
        req.setResponses(responses);
        req.setOwnerId(ownerId);
        return req;
    }


    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("user1");
        userDto.setEmail("user1@mail.ru");

        mockedUser = Mockito.mock(User.class);
        when(mockedUser.getId()).thenReturn(1L);
        when(mockedUser.getName()).thenReturn("user1");
        when(mockedUser.getEmail()).thenReturn("user1@mail.ru");

        mockUserQuery = Mockito.mock(TypedQuery.class);

        when(userService.createUser(Mockito.any(UserDto.class)))
                .thenReturn(userDto);

        when(mockUserQuery.setParameter(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(mockUserQuery);

        when(mockUserQuery.getSingleResult())
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
        when(mockedItem.getName()).thenReturn("item1");
        when(mockedItem.getDescription()).thenReturn("item1Descr");
        when(mockedItem.getOwnerId()).thenReturn(1L);

        mockItemQuery = Mockito.mock(TypedQuery.class);

        when(itemService.addItem(Mockito.anyLong(), Mockito.any(ItemDto.class)))
                .thenReturn(itemDto);

        when(mockItemQuery.setParameter(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(mockItemQuery);

        when(mockItemQuery.getSingleResult())
                .thenReturn(mockedItem);

        itemService.addItem(mockedUser.getId(), itemDto);

        //------------------------------------------

        //request

        request = new Request();
        request.setId(1L);
        request.setDescription("requestDescription");
        request.setOwnerId(mockedUser.getId());

        mockedRequest = Mockito.mock(Request.class);
        when(mockedRequest.getId()).thenReturn(1L);
        when(mockedRequest.getDescription()).thenReturn("requestDescription");
        when(mockedRequest.getCreated()).thenReturn(LocalDateTime.now());
        when(mockedRequest.getOwnerId()).thenReturn(1L);

        mockRequestQuery = Mockito.mock(TypedQuery.class);

        when(requestService.createRequest(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(request);

        when(mockRequestQuery.setParameter(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(mockRequestQuery);

        when(mockRequestQuery.getSingleResult())
                .thenReturn(request);

        when(entityManager.createQuery(Mockito.anyString(), Mockito.eq(Request.class)))
                .thenReturn(mockRequestQuery);

        requestService.createRequest(mockedUser.getId(), "requestDescription");
    }

    @Test
    void createRequest() {
        when(requestRepository.save(Mockito.any(Request.class))).thenReturn(request);

        Request request1 = requestServiceImpl.createRequest(1L, "{\n" +
                "    \"text\": \"sOIemDZOdHp20znydfwoXzQgsO2MDD10VqwWs4DHIJtjpBs7Y2\"\n" +
                "}");

        assertEquals("sOIemDZOdHp20znydfwoXzQgsO2MDD10VqwWs4DHIJtjpBs7Y2", request1.getDescription());
    }

    @Test
    void getRequestWithResponsesTest() {
        when(requestRepository.findAllByUserIdFetchResponses(Mockito.anyLong())).thenReturn(List.of(request));

        List<Request> req = requestServiceImpl.getUserRequestsWithResponses(1L);

        assertThat(req.size(), is(1));
    }
}