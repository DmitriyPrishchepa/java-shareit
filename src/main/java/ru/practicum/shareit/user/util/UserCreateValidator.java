package ru.practicum.shareit.user.util;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.exceptions.DuplicateException;
import ru.practicum.shareit.exception.exceptions.MissingParameterException;
import ru.practicum.shareit.exception.exceptions.UserValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.Map;

@Component
public class UserCreateValidator {

    public static void validateCreateUser(User user, Map<Long, User> users) {

        if (user.getName() == null) {
            throw new MissingParameterException("Name cannot be null");
        }

        if (user.getEmail() == null) {
            throw new MissingParameterException("Email cannot be null");
        }

        for (User u : users.values()) {
            if (u.getEmail().equals(user.getEmail())) {
                throw new DuplicateException("Email already exists");
            }
        }

        if (!user.getEmail().contains("@")) {
            throw new UserValidationException("Email is invalid");
        }
    }
}
