package json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {UserMapper.class})
public class UserMapperTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    void testMapToDto() {
        User user = new User(1L, "Имя пользователя", "user@example.com");

        UserDto userDto = userMapper.mapToDto(user);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void testMapFromDto() {
        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setName("Другое имя");
        userDto.setEmail("other@example.com");

        User user = userMapper.mapFromDto(userDto);

        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }
}
