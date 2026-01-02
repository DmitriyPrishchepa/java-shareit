package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.exceptions.MissingParameterException;
import ru.practicum.exception.exceptions.UserValidationException;

@RestControllerAdvice
@Slf4j
public class GatewayGlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handelMissingParameter(MissingParameterException e) {
        log.error("Missing parameter");
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(UserValidationException e) {
        log.error("Not valid data");
        return new ErrorResponse(e.getMessage());
    }
}
