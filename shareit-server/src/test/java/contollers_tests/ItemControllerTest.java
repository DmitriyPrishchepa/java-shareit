package contollers_tests;

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
import ru.practicum.shareit.item.dto.CommentDtoToReturn;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.dto.ItemWithCommentsToReturn;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    @Mock
    ItemService itemService;

    @InjectMocks
    ItemController itemController;

    @Mock
    UserService userService;

    @InjectMocks
    UserController userController;

    private final ObjectMapper mapper = new ObjectMapper();

    private ItemDto itemDto;
    private ItemDto itemDto2;
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

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemController, userController)
                .build();

        itemDto = makeItemDto(
                1L,
                1L,
                "itemDto",
                "itemDescription",
                true
        );

        itemDto2 = makeItemDto(
                2L,
                1L,
                "itemDto2",
                "itemDescription2",
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
    }

    @Test
    void saveItemTest() throws Exception {

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

    }

    @Test
    void updateItemTest() throws Exception {

        ItemDto updatedItemDto = makeItemDto(
                1L,
                1L,
                "updatedItem",
                "updatedDescription",
                true
        );

        when(itemService.updateItem(userDto.getId(), itemDto.getId(), updatedItemDto))
                .thenReturn(updatedItemDto);

        mockMvc.perform(patch("/items/" + itemDto.getId())
                        .header("X-Sharer-User-Id", userDto.getId())
                        .content(mapper.writeValueAsString(updatedItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(updatedItemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(updatedItemDto.getDescription())));
    }

    @Test
    void getItemByIdTest() throws Exception {

        ItemWithCommentsToReturn item = new ItemWithCommentsToReturn();
        item.setId(itemDto2.getId());
        item.setName(itemDto2.getName());
        item.setAvailable(itemDto2.getAvailable());
        item.setOwnerId(itemDto2.getOwnerId());
        item.setComments(new ArrayList<>());

        when(itemService.getItemById(userDto.getId(), itemDto2.getId()))
                .thenReturn(item);

        mockMvc.perform(get("/items/" + itemDto2.getId())
                        .header("X-Sharer-User-Id", userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(item.getName()), String.class))
                .andExpect(jsonPath("$.description", is(item.getDescription())));
    }

    @Test
    void getALlItemsFromUserTest() throws Exception {

        ItemDtoWithBookings item = new ItemDtoWithBookings();
        item.setId(itemDto2.getId());
        item.setName(itemDto2.getName());
        item.setAvailable(itemDto2.getAvailable());
        item.setBookings(new ArrayList<>());

        ItemDtoWithBookings item2 = new ItemDtoWithBookings();
        item2.setId(itemDto2.getId());
        item2.setName(itemDto2.getName());
        item2.setAvailable(itemDto2.getAvailable());
        item2.setBookings(new ArrayList<>());


        when(itemService.getAllItemsFromUser(userDto.getId()))
                .thenReturn(List.of(item, item2));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name", is(item.getName()), String.class))
                .andExpect(jsonPath("$[0].description", is(item2.getDescription()), String.class));

    }

    @Test
    void searchItemTest() throws Exception {

        List<ItemDto> items = List.of(itemDto, itemDto2);

        when(itemService.searchItem(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .param("text", "emD")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(items.size())))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder(itemDto.getName(), itemDto2.getName())));
    }

    @Test
    void addCommentTest() throws Exception {
        CommentDtoToReturn commentDto = new CommentDtoToReturn();
        commentDto.setId(1L);
        commentDto.setText("this comment just for you");
        commentDto.setAuthorName(userDto.getName());
        commentDto.setCreated(LocalDateTime.now());

        when(itemService.addComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/" + itemDto.getId() + "/comment")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .content("this comment just for you")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId().intValue())))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));
    }
}
