package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userRepository.createUser(mapFromDto(userDto));
        return mapToDto(user);
    }

    @Override
    public UserDto updateUser(long id, UserDto userDto) {
        User user = userRepository.updateUser(id, mapFromDto(userDto));
        return mapToDto(user);
    }

    @Override
    public UserDto getUserById(long id) {
        User user = userRepository.getUserById(id);
        return mapToDto(user);
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteUser(id);
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.getUsers().values()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private User mapFromDto(UserDto userDto) {
        return userMapper.mapFromDto(userDto);
    }

    private UserDto mapToDto(User user) {
        return userMapper.mapToDto(user);
    }
}
