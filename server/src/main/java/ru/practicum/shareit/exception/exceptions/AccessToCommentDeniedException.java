package ru.practicum.shareit.exception.exceptions;

public class AccessToCommentDeniedException extends RuntimeException {
    public AccessToCommentDeniedException(String message) {
        super(message);
    }
}
