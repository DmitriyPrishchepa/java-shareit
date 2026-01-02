package ru.practicum.users.validation;

import ru.practicum.exception.exceptions.MissingParameterException;
import ru.practicum.exception.exceptions.UserValidationException;
import ru.practicum.users.dto.UserDto;

public class UserValidator {

    public static void validateUser(UserDto user) {

        if (user.getEmail() == null) {
            throw new MissingParameterException("Email cannot be null");
        }

        if (!user.getEmail().contains("@")) {
            throw new UserValidationException("Email is invalid");
        }
    }
}
