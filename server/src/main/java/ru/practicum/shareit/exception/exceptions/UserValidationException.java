package ru.practicum.shareit.exception.exceptions;

public class UserValidationException extends RuntimeException {
    public UserValidationException(String message) {
        super(message);
    }
}
