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
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private UserDto userDto;
    private UserDto userDto2;

    private List<UserDto> usersList;

    // Регистрация модуля для работы с LocalDate
    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("userDto1");
        userDto.setEmail("user1dto@mail.ru");

        userDto2 = new UserDto();
        userDto2.setId(1L);
        userDto2.setName("userDto2");
        userDto2.setEmail("user2dto@mail.ru");

        usersList = List.of(userDto, userDto2);
    }

    @Test
    void saveNewUser() throws Exception {
        when(userService.createUser(any()))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void updateUserTest() throws Exception {
        UserDto updatedDto = new UserDto();
        updatedDto.setId(1L);
        updatedDto.setName("updatedDto");
        updatedDto.setEmail("user1dto@mail.ru");

        when(userService.updateUser(Mockito.anyLong(), Mockito.any(UserDto.class)))
                .thenReturn(updatedDto);

        mvc.perform(patch("/users/" + userDto.getId())
                        .header("X-Sharer-User-Id", userDto.getId())
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(updatedDto.getName()), String.class));
    }

    @Test
    void getUserByIdTest() throws Exception {
        when(userService.getUserById(Mockito.anyLong()))
                .thenReturn(userDto2);

        mvc.perform(get("/users/" + userDto2.getId())
                        .header("X-Sharer-User-Id", userDto2.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto2.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto2.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto2.getEmail()), String.class));
    }

    @Test
    void deleteUserTest() throws Exception {
        mvc.perform(delete("/users/" + userDto.getId())
                        .header("X-Sharer-User-Id", userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService).deleteUser(userDto.getId());
    }
}
