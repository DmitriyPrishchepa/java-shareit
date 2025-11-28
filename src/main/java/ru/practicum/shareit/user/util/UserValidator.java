package ru.practicum.shareit.user.util;

import ru.practicum.shareit.exception.exceptions.MissingParameterException;
import ru.practicum.shareit.exception.exceptions.UserValidationException;
import ru.practicum.shareit.user.model.User;

public class UserValidator {

    public static void validateUser(User user) {

        if (user.getEmail() == null) {
            throw new MissingParameterException("Email cannot be null");
        }

        if (!user.getEmail().contains("@")) {
            throw new UserValidationException("Email is invalid");
        }
    }
}
