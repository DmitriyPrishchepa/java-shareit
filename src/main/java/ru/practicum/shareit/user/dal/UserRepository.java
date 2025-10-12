package ru.practicum.shareit.user.dal;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.Map;

@Repository
public interface UserRepository {
    User createUser(User user);

    User updateUser(long id, User user);

    User getUserById(long id);

    void deleteUser(long id);

    Map<Long, User> getUsers();
}
