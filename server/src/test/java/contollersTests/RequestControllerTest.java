package contollersTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestController;
import ru.practicum.shareit.request.RequestService;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.model.Response;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RequestControllerTest {

    @Mock
    ItemService itemService;

    @InjectMocks
    ItemController itemController;

    @Mock
    UserService userService;

    @InjectMocks
    UserController userController;

    @Mock
    RequestService requestService;

    @InjectMocks
    RequestController requestController;

    private final ObjectMapper mapper = new ObjectMapper();

    private ItemDto itemDto;
    private Request request;
    private UserDto userDto;
    private MockMvc mockMvc;

    private ItemDto makeItemDto(
            Long id,
            Long ownerId,
            String name,
            String description,
            Boolean available
    ) {
        ItemDto dto = new ItemDto();
        dto.setId(id);
        dto.setOwnerId(ownerId);
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        dto.setRequestId(null);

        return dto;
    }

    private UserDto makeUserDto(
            Long id,
            String name,
            String email
    ) {
        UserDto dto = new UserDto();
        dto.setId(id);
        dto.setName(name);
        dto.setEmail(email);

        return dto;
    }

    private Request makeRequest(
            long id,
            String description,
            LocalDateTime created,
            Long ownerId,
            List<Response> responses,
            List<Item> items
    ) {
        Request request = new Request();
        request.setId(id);
        request.setDescription(description);
        request.setOwnerId(ownerId);
        request.setItems(new ArrayList<>());
        request.setResponses(new ArrayList<>());
        return request;
    }

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemController, userController, requestController)
                .build();

        itemDto = makeItemDto(
                1L,
                1L,
                "itemDto",
                "itemDescription",
                true
        );

        userDto = makeUserDto(1L, "userDto", "userEmail");

        when(userService.createUser(any()))
                .thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class));

        when(itemService.addItem(userDto.getId(), itemDto))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class));

        request = makeRequest(
                1L,
                "test description",
                LocalDateTime.now(),
                userDto.getId(),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    @Test
    void createRequestTest() throws Exception {

        when(requestService.createRequest(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(request);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(request.getDescription())))
                .andExpect(jsonPath("$.responses", is(request.getResponses())));
    }

    @Test
    void getRequestByIdTest() throws Exception {
        when(requestService.getRequestById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(request);

        mockMvc.perform(get("/requests/" + request.getId())
                        .header("X-Sharer-User-Id", userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(request.getDescription())))
                .andExpect(jsonPath("$.responses", is(request.getResponses())));
    }

    @Test
    void getOtherUsersRequests() throws Exception {

        Request requestOfUser2 = makeRequest(
                2L,
                "test description 2",
                LocalDateTime.now(),
                2L,
                new ArrayList<>(),
                new ArrayList<>()
        );

        Request requestOfUser3 = makeRequest(
                3L,
                "test description 3",
                LocalDateTime.now(),
                3L,
                new ArrayList<>(),
                new ArrayList<>()
        );

        List<Request> requests = List.of(requestOfUser2, requestOfUser3);

        when(requestService.getOtherUsersRequests(Mockito.anyLong()))
                .thenReturn(requests);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(mvcResult -> {
                    String response = mvcResult.getResponse().getContentAsString();
                    System.out.println("Response: " + response);
                })
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(requests.size())))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(
                        (int) requestOfUser2.getId(), (int) requestOfUser3.getId())));
    }

    @Test
    void getUserRequestsWithResponsesTest() throws Exception {
        Response response1 = new Response();
        response1.setId(1L);
        response1.setName("response1");
        response1.setRequest(request);
        response1.setItemId(itemDto.getId());
        response1.setOwnerId(1L);

        Response response2 = new Response();
        response2.setId(2L);
        response2.setName("response2");
        response2.setRequest(request);
        response2.setItemId(itemDto.getId());
        response2.setOwnerId(2L);

        List<Response> responses = List.of(response1, response2);

        request.setResponses(responses);

        System.out.println("Request responses: " + request.getResponses());

        when(requestService.getUserRequestsWithResponses(Mockito.anyLong()))
                .thenReturn(List.of(request));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(mvcResult -> {
                    String response = mvcResult.getResponse().getContentAsString();
                    System.out.println("Response: " + response);
                })
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].responses", hasSize(2)));
    }
}
