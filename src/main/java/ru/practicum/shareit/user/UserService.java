package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Map;

@Service
public interface UserService {
    User createUser(UserDto userDto);

    User updateUser(long id, UserDto user);

    User getUserById(long id);

    void deleteUser(long id);

    Map<Long, User> getUsers();
}
