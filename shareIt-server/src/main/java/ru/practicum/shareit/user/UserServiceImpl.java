package ru.practicum.shareit.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.EntityUtils;
import ru.practicum.shareit.exception.exceptions.DuplicateException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.util.UserValidator;

@Service
@RequiredArgsConstructor
@Getter
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EntityUtils entityUtils;

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {

        User userWithEmail = userRepository.findUserByEmailContaining(userDto.getEmail());

        if (userWithEmail != null) {
            throw new DuplicateException("Email already exists");
        }

        UserValidator.validateUser(userMapper.mapFromDto(userDto));

        User user = userRepository.save(userMapper.mapFromDto(userDto));
        return userMapper.mapToDto(user);
    }

    @Transactional
    @Override
    public UserDto updateUser(long id, UserDto userDto) {

        entityUtils.checkExistingUser(id);

        User user = userRepository.getReferenceById(id);

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            if (userRepository.existsByEmail(userDto.getEmail())) {
                throw new DuplicateException("Email already in use by another user");
            }
            user.setEmail(userDto.getEmail());
        }

        User savedUser = userRepository.save(user);
        return userMapper.mapToDto(savedUser);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getUserById(long id) {
        entityUtils.checkExistingUser(id);
        User user = userRepository.getReferenceById(id);
        return userMapper.mapToDto(user);
    }

    @Transactional
    @Override
    public void deleteUser(long id) {
        entityUtils.checkExistingUser(id);
        userRepository.deleteById(id);
    }
}
