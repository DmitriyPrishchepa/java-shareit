package ru.practicum.shareit.user.dal;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.exceptions.AlreadyExistsException;
import ru.practicum.shareit.exception.exceptions.ElementNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.util.UserCreateValidator;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final AtomicLong userIdGenerator = new AtomicLong(0);

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User createUser(User user) {
        UserCreateValidator.validateCreateUser(user, users);
        Long userId = generateUserId(); // Генерируем ID один раз
        user.setId(userId);
        users.put(userId, user); // Используем тот же ID
        return user;
    }

    @Override
    public User updateUser(long id, User newUser) {
        User user1 = getUserById(id);

        for (User u : users.values()) {
            if (!u.getId().equals(user1.getId()) && u.getEmail().equals(newUser.getEmail())) {
                throw new AlreadyExistsException("User with this email already exists");
            }
        }

        user1.setEmail(newUser.getEmail());
        user1.setName(newUser.getName());
        users.put(user1.getId(), user1);
        return getUserById(user1.getId());
    }

    @Override
    public User getUserById(long id) {
        User user = users.get(id);
        if (user == null) {
            throw new ElementNotFoundException("User not found");
        }
        return user;
    }

    @Override
    public void deleteUser(long id) {
        users.remove(id);
    }

    @Override
    public Map<Long, User> getUsers() {
        return new HashMap<>(users);
    }

    public Long generateUserId() {
        return userIdGenerator.getAndIncrement();
    }
}
