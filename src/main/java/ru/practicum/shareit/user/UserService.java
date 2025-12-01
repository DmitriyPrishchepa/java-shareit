package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto updateUser(long id, UserDto user);

    UserDto getUserById(long id);

    void deleteUser(long id);

    List<UserDto> getUsers();
}
