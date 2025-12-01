package ru.practicum.shareit.user.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Component
public class UserMapper {

    private final ModelMapper modelMapper = new ModelMapper();

    public UserDto mapToDto(User user) {
        return this.modelMapper.map(user, UserDto.class);
    }

    public User mapFromDto(UserDto userDto) {
        return this.modelMapper.map(userDto, User.class);
    }
}
