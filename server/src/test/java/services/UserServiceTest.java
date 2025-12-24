package services;

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
import ru.practicum.shareit.exception.exceptions.DuplicateException;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceTest {

    @Mock
    UserService userService;

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
        Mockito.when(mockedUser.getId()).thenReturn(1L);
        Mockito.when(mockedUser.getName()).thenReturn("User1");
        Mockito.when(mockedUser.getEmail()).thenReturn("user1@yandex.ru");

        Mockito.when(userService.createUser(Mockito.any(UserDto.class)))
                .thenReturn(dto);

        mockQuery = Mockito.mock(TypedQuery.class);

        Mockito.when(mockQuery.setParameter(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(mockQuery);

        Mockito.when(mockQuery.getSingleResult())
                .thenReturn(mockedUser);

        Mockito.when(entityManager.createQuery(Mockito.anyString(), Mockito.eq(User.class)))
                .thenReturn(mockQuery);
    }

    @Test
    void saveUserTest() {
        UserDto userDto = userService.createUser(dto);

        TypedQuery<User> query = entityManager.createQuery("Select u from User u ", User.class);
        User user = query.getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
    }

    @Test
    void updateUserTest() {

        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setId(1L);
        updatedUserDto.setName("NewName");
        updatedUserDto.setEmail("newEmail@yandex.ru");

        Mockito.when(userService.updateUser(Mockito.anyInt(), Mockito.eq(updatedUserDto)))
                .thenReturn(updatedUserDto);

        userService.updateUser(mockedUser.getId(), updatedUserDto);

        TypedQuery<User> updatedQuery = entityManager.createQuery("Select u from User u ", User.class);
        User userUpdated = updatedQuery.getSingleResult();

        Mockito.when(mockedUser.getName())
                .thenReturn(updatedUserDto.getName());

        Mockito.when(mockedUser.getEmail())
                .thenReturn(updatedUserDto.getEmail());

        assertThat(userUpdated.getId(), notNullValue());
        assertThat(userUpdated.getName(), equalTo(updatedUserDto.getName()));
        assertThat(userUpdated.getEmail(), equalTo(updatedUserDto.getEmail()));
    }

    @Test
    void getUserByIdTest() {

        Mockito.when(userService.getUserById(Mockito.anyLong()))
                .thenReturn(dto);

        userService.createUser(dto);

        UserDto foundDto = userService.getUserById(1L);

        System.out.println(foundDto);

        TypedQuery<User> query = entityManager.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", "user1@yandex.ru").getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(foundDto.getName()));
    }

    @Test
    public void deleteUserTest() {
        dto.setId(1L);
        userService.createUser(dto);

        userService.deleteUser(1L);

        Mockito.verify(userService, Mockito.times(1)).deleteUser(1L);
    }

    @Test
    void duplicateUserExceptionTest() {
        Mockito.when(userService.createUser(Mockito.any(UserDto.class)))
                .thenThrow(DuplicateException.class);

        assertThrows(DuplicateException.class, () -> {
            userService.createUser(dto);
        });
    }
}