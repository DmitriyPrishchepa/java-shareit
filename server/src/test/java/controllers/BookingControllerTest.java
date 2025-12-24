package controllers;

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
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoToReturn;
import ru.practicum.shareit.booking.util.BookingState;
import ru.practicum.shareit.booking.util.BookingStateSearchParams;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {

    @Mock
    ItemService itemService;

    @InjectMocks
    ItemController itemController;

    @Mock
    UserService userService;

    @InjectMocks
    UserController userController;

    @Mock
    BookingService bookingService;

    @InjectMocks
    BookingController bookingController;

    private final ObjectMapper mapper = new ObjectMapper();
    private ItemDto itemDto;
    private BookingDto bookingDto;
    private UserDto userDto;
    private MockMvc mockMvc;
    private BookingDtoToReturn result;

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

    private BookingDto makeBookingDto(
            Long itemId,
            LocalDateTime start,
            LocalDateTime end,
            BookingState status
    ) {
        BookingDto dto = new BookingDto();
        dto.setItemId(itemDto.getId());
        dto.setStart(LocalDateTime.now());
        dto.setEnd(LocalDateTime.now());
        dto.setStatus(status);
        return dto;
    }

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemController, userController, bookingController)
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

        bookingDto = makeBookingDto(1L, LocalDateTime.now(), LocalDateTime.now(), BookingState.WAITING);

        result = new BookingDtoToReturn();
        result.setId(1L);
        result.setStart(bookingDto.getStart());
        result.setEnd(bookingDto.getEnd());
        result.setStatus(bookingDto.getStatus());
    }

    @Test
    void createBookingTest() throws Exception {

        when(bookingController.createBooking(Mockito.anyLong(), Mockito.any(BookingDto.class)))
                .thenReturn(result);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(result.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(result.getStatus().toString()), BookingState.class));

    }

    @Test
    void updateBookingApprovalTest() throws Exception {

        BookingDtoToReturn approvedDto = new BookingDtoToReturn();
        approvedDto.setId(1L);
        approvedDto.setStart(bookingDto.getStart());
        approvedDto.setEnd(bookingDto.getEnd());
        approvedDto.setStatus(BookingState.APPROVED);

        BookingDtoToReturn rejectedDto = new BookingDtoToReturn();
        rejectedDto.setId(1L);
        rejectedDto.setStart(bookingDto.getStart());
        rejectedDto.setEnd(bookingDto.getEnd());
        rejectedDto.setStatus(BookingState.REJECTED);

        when(bookingController.updateBookingApproval(Mockito.anyLong(), Mockito.anyString(), Mockito.eq(true)))
                .thenReturn(approvedDto);

        mockMvc.perform(patch("/bookings/" + result.getId())
                        .header("X-Sharer-User-Id", userDto.getId())
                        .param("approved", String.valueOf(true))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(BookingState.APPROVED.toString())));

        when(bookingController.updateBookingApproval(Mockito.anyLong(), Mockito.anyString(), Mockito.eq(false)))
                .thenReturn(rejectedDto);

        mockMvc.perform(patch("/bookings/" + result.getId())
                        .header("X-Sharer-User-Id", userDto.getId())
                        .param("approved", String.valueOf(false))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(BookingState.REJECTED.toString())));
    }

    @Test
    void getBookingByIdTest() throws Exception {

        when(bookingController.getBookingById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(result);

        mockMvc.perform(get("/bookings/" + result.getId())
                        .header("X-Sharer-User-Id", userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(result.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(BookingState.WAITING.toString())));
    }

    @Test
    void getBookingsByStateTest_PAST() throws Exception {

        BookingDtoToReturn result2 = new BookingDtoToReturn();
        result2.setId(2L);
        result2.setStart(LocalDateTime.of(
                2024,
                10,
                12,
                12,
                13,
                14
        ));
        result2.setEnd(LocalDateTime.of(
                2025,
                10,
                14,
                12,
                13,
                14
        ));
        result2.setStatus(BookingState.CANCELED);

        BookingDtoToReturn result3 = new BookingDtoToReturn();
        result3.setId(3L);
        result3.setStart(LocalDateTime.of(
                2023,
                10,
                12,
                12,
                13,
                14
        ));
        result3.setEnd(LocalDateTime.of(
                2024,
                10,
                14,
                12,
                13,
                14
        ));
        result3.setStatus(BookingState.CANCELED);

        List<BookingDtoToReturn> returnedResults = List.of(result2, result3);

        when(bookingController.getBookingsByState(1L, BookingStateSearchParams.PAST.name()))
                .thenReturn(returnedResults);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .param("state", BookingStateSearchParams.PAST.name())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getBookingsByStateTest_CURRENT() throws Exception {

        BookingDtoToReturn result2 = new BookingDtoToReturn();
        result2.setId(2L);
        result2.setStart(LocalDateTime.of(
                2024,
                10,
                12,
                12,
                13,
                14
        ));
        result2.setEnd(LocalDateTime.of(
                2026,
                10,
                14,
                12,
                13,
                14
        ));
        result2.setStatus(BookingState.APPROVED);

        BookingDtoToReturn result3 = new BookingDtoToReturn();
        result3.setId(3L);
        result3.setStart(LocalDateTime.of(
                2023,
                10,
                12,
                12,
                13,
                14
        ));
        result3.setEnd(LocalDateTime.of(
                2026,
                10,
                14,
                12,
                13,
                14
        ));
        result3.setStatus(BookingState.APPROVED);

        List<BookingDtoToReturn> returnedResults = List.of(result2, result3);

        when(bookingController.getBookingsByState(1L, BookingStateSearchParams.CURRENT.name()))
                .thenReturn(returnedResults);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .param("state", BookingStateSearchParams.CURRENT.name())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getBookingsByStateTest_FUTURE() throws Exception {

        BookingDtoToReturn result2 = new BookingDtoToReturn();
        result2.setId(2L);
        result2.setStart(LocalDateTime.of(
                2026,
                10,
                12,
                12,
                13,
                14
        ));
        result2.setEnd(LocalDateTime.of(
                2027,
                10,
                14,
                12,
                13,
                14
        ));
        result2.setStatus(BookingState.APPROVED);

        BookingDtoToReturn result3 = new BookingDtoToReturn();
        result3.setId(3L);
        result3.setStart(LocalDateTime.of(
                2026,
                10,
                12,
                12,
                13,
                14
        ));
        result3.setEnd(LocalDateTime.of(
                2027,
                10,
                14,
                12,
                13,
                14
        ));
        result3.setStatus(BookingState.WAITING);

        List<BookingDtoToReturn> returnedResults = List.of(result2, result3);

        when(bookingController.getBookingsByState(1L, BookingStateSearchParams.FUTURE.name()))
                .thenReturn(returnedResults);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .param("state", BookingStateSearchParams.FUTURE.name())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getBookingsByStateTest_WAITING() throws Exception {

        BookingDtoToReturn result2 = new BookingDtoToReturn();
        result2.setId(2L);
        result2.setStart(LocalDateTime.of(
                2024,
                10,
                12,
                12,
                13,
                14
        ));
        result2.setEnd(LocalDateTime.of(
                2027,
                10,
                14,
                12,
                13,
                14
        ));
        result2.setStatus(BookingState.WAITING);

        BookingDtoToReturn result3 = new BookingDtoToReturn();
        result3.setId(3L);
        result3.setStart(LocalDateTime.of(
                2024,
                10,
                12,
                12,
                13,
                14
        ));
        result3.setEnd(LocalDateTime.of(
                2027,
                10,
                14,
                12,
                13,
                14
        ));
        result3.setStatus(BookingState.WAITING);

        List<BookingDtoToReturn> returnedResults = List.of(result2, result3);

        when(bookingController.getBookingsByState(1L, BookingStateSearchParams.WAITING.name()))
                .thenReturn(returnedResults);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .param("state", BookingStateSearchParams.WAITING.name())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getBookingsByStateTest_ALL() throws Exception {

        BookingDtoToReturn result2 = new BookingDtoToReturn();
        result2.setId(2L);
        result2.setStart(LocalDateTime.of(
                2026,
                10,
                12,
                12,
                13,
                14
        ));
        result2.setEnd(LocalDateTime.of(
                2027,
                10,
                14,
                12,
                13,
                14
        ));
        result2.setStatus(BookingState.APPROVED);

        BookingDtoToReturn result3 = new BookingDtoToReturn();
        result3.setId(3L);
        result3.setStart(LocalDateTime.of(
                2026,
                10,
                12,
                12,
                13,
                14
        ));
        result3.setEnd(LocalDateTime.of(
                2027,
                10,
                14,
                12,
                13,
                14
        ));
        result3.setStatus(BookingState.WAITING);

        List<BookingDtoToReturn> returnedResults = List.of(result2, result3);

        when(bookingController.getBookingsByState(1L, BookingStateSearchParams.ALL.name()))
                .thenReturn(returnedResults);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .param("state", BookingStateSearchParams.ALL.name())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }


    @Test
    void getAllBookingsTest_WAITING() throws Exception {

        List<BookingDto> returnedResults = List.of(bookingDto);

        when(bookingController.getAllBookings(1L, BookingStateSearchParams.WAITING.name()))
                .thenReturn(returnedResults);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .param("state", BookingStateSearchParams.WAITING.name())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].status", is(BookingState.WAITING.name())));
    }

    @Test
    void getAllBookingsTest_CURRENT() throws Exception {
        bookingDto.setStatus(BookingState.APPROVED);

        List<BookingDto> returnedResults = List.of(bookingDto);

        when(bookingController.getAllBookings(1L, BookingStateSearchParams.CURRENT.name()))
                .thenReturn(returnedResults);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .param("state", BookingStateSearchParams.CURRENT.name())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].status", is(BookingState.APPROVED.name())));
    }

    @Test
    void getAllBookingsTest_PAST() throws Exception {

        bookingDto.setStatus(BookingState.CANCELED);

        List<BookingDto> returnedResults = List.of(bookingDto);

        when(bookingController.getAllBookings(1L, BookingStateSearchParams.PAST.name()))
                .thenReturn(returnedResults);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .param("state", BookingStateSearchParams.PAST.name())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].status", is(BookingState.CANCELED.name())));
    }

    @Test
    void getAllBookingsTest_FUTURE() throws Exception {

        bookingDto.setStatus(BookingState.WAITING);

        List<BookingDto> returnedResults = List.of(bookingDto);

        when(bookingController.getAllBookings(1L, BookingStateSearchParams.FUTURE.name()))
                .thenReturn(returnedResults);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .param("state", BookingStateSearchParams.FUTURE.name())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].status", is(BookingState.WAITING.name())));
    }
}
