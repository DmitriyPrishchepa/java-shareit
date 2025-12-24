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
import ru.practicum.shareit.exception.exceptions.DuplicateException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceImplTest {
    @Mock
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @InjectMocks
    UserServiceImpl userServiceImpl;

    @Mock
    EntityManager entityManager;

    UserDto dto;
    TypedQuery<User> mockQuery;
    User mockedUser;

    private UserDto makeUserDto(
            String name,
            String email
    ) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }

    @BeforeEach
    void setUp() {
        dto = makeUserDto("User1", "user1@yandex.ru");

        mockedUser = Mockito.mock(User.class);
        when(mockedUser.getId()).thenReturn(1L);
        when(mockedUser.getName()).thenReturn("User1");
        when(mockedUser.getEmail()).thenReturn("user1@yandex.ru");

        when(userService.createUser(Mockito.any(UserDto.class)))
                .thenReturn(dto);

        mockQuery = Mockito.mock(TypedQuery.class);

        when(mockQuery.setParameter(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(mockQuery);

        when(mockQuery.getSingleResult())
                .thenReturn(mockedUser);

        when(entityManager.createQuery(Mockito.anyString(), Mockito.eq(User.class)))
                .thenReturn(mockQuery);
    }

    @Test
    void saveUserTest() {
        Mockito.when(userRepository.findUserByEmailContaining(Mockito.anyString())).thenReturn(null);
        Mockito.when(userMapper.mapToDto(Mockito.any(User.class))).thenReturn(dto);
        Mockito.when(userMapper.mapFromDto(Mockito.any(UserDto.class))).thenReturn(mockedUser);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(mockedUser);

        UserDto userDto = userServiceImpl.createUser(dto);

        assertEquals("User1", userDto.getName());
        assertEquals("user1@yandex.ru", userDto.getEmail());
    }

    @Test
    void saveUser_DuplicateEmail() {
        when(userRepository.findUserByEmailContaining(Mockito.anyString()))
                .thenReturn(mockedUser);

        when(userMapper.mapFromDto(Mockito.any(UserDto.class)))
                .thenReturn(mockedUser);

        when(userMapper.mapToDto(Mockito.any(User.class)))
                .thenReturn(dto);

        try {
            userServiceImpl.createUser(dto);
        } catch (DuplicateException e) {
            assertEquals("Email already exists", e.getMessage());
        }
    }

    @Test
    void updateUserTest_Success() {
        when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(mockedUser);
        when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
        when(userRepository.save(Mockito.any(User.class))).thenReturn(mockedUser);

        when(userMapper.mapToDto(Mockito.any(User.class)))
                .thenReturn(dto);

        UserDto user = userServiceImpl.updateUser(1L, dto);

        assertNotNull(user);
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getEmail(), dto.getEmail());
    }

    @Test
    void updateUserTest_Duplicate() {
        when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(mockedUser);
        when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
        when(userRepository.save(Mockito.any(User.class))).thenReturn(mockedUser);

        when(userMapper.mapToDto(Mockito.any(User.class)))
                .thenReturn(dto);

        try {
            userServiceImpl.updateUser(1L, dto);
        } catch (DuplicateException e) {
            assertEquals("Email already in use by another user", e.getMessage());
        }
    }

    @Test
    void getUserById() {
        Mockito.when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(mockedUser);
        Mockito.when(userMapper.mapToDto(Mockito.any(User.class))).thenReturn(dto);

        UserDto returnedDto = userServiceImpl.getUserById(1L);

        assertEquals("User1", returnedDto.getName());
        assertEquals("user1@yandex.ru", returnedDto.getEmail());
    }
}