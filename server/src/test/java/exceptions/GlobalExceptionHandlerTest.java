package exceptions;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ErrorResponse;
import ru.practicum.shareit.exception.GlobalExceptionHandler;
import ru.practicum.shareit.exception.exceptions.*;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void testHandleResourceReturnNull() {
        // Создаем исключение
        NullPointerException exception = new NullPointerException("Element doesn't exist");

        // Вызываем метод обработчика
        ErrorResponse response = globalExceptionHandler.handleResourceReturnNull(exception);

        // Проверяем ответ
        assertThat(response.getError()).isEqualTo("Element doesn't exist");
    }

    @Test
    void testHandleNotFound() {
        // Создаем исключение
        ElementNotFoundException exception = new ElementNotFoundException("Element not found");

        // Вызываем метод обработчика
        ErrorResponse response = globalExceptionHandler.handleNotFound(exception);

        // Проверяем ответ
        assertThat(response.getError()).isEqualTo("Element not found");
    }

    @Test
    void testHandleValidation() {
        // Создаем исключение
        UserValidationException exception = new UserValidationException("Not valid data");

        // Вызываем метод обработчика
        ErrorResponse response = globalExceptionHandler.handleValidation(exception);

        // Проверяем ответ
        assertThat(response.getError()).isEqualTo("Not valid data");
    }

    @Test
    void testHandleDuplicate() {
        // Создаем исключение
        DuplicateException exception = new DuplicateException("Duplicate");

        // Вызываем метод обработчика
        ErrorResponse response = globalExceptionHandler.handleDuplicate(exception);

        // Проверяем ответ
        assertThat(response.getError()).isEqualTo("Duplicate");
    }

    @Test
    void testHandleMissingParameter() {
        // Создаем исключение
        MissingParameterException exception = new MissingParameterException("Missing parameter");

        // Вызываем метод обработчика
        ErrorResponse response = globalExceptionHandler.handelMissingParameter(exception);

        // Проверяем ответ
        assertThat(response.getError()).isEqualTo("Missing parameter");
    }

    @Test
    void testManageUnhandledExceptions() {
        // Создаем исключение
        Exception exception = new Exception("Unhandled exception");

        // Вызываем метод обработчика
        ErrorResponse response = globalExceptionHandler.manageUnhandledExceptions(exception);

        // Проверяем ответ
        assertThat(response.getError()).isEqualTo("Unhandled exception");
    }

    @Test
    void testHandleBookingValidation() {
        // Создаем исключение
        BookingValidationException exception = new BookingValidationException("Booking Validation exception");

        // Вызываем метод обработчика
        ErrorResponse response = globalExceptionHandler.handleBookingValidation(exception);

        // Проверяем ответ
        assertThat(response.getError()).isEqualTo("Booking Validation exception");
    }

    @Test
    void testHandeWrongUser() {
        // Создаем исключение
        WrongUserException exception = new WrongUserException("Wrong user");

        // Вызываем метод обработчика
        ErrorResponse response = globalExceptionHandler.handeWrongUser(exception);

        // Проверяем ответ
        assertThat(response.getError()).isEqualTo("Wrong user");
    }

    @Test
    void testHandeAccessDenied() {
        // Создаем исключение
        AccessToCommentDeniedException exception = new AccessToCommentDeniedException("Access denied");

        // Вызываем метод обработчика
        ErrorResponse response = globalExceptionHandler.handeAccessDenied(exception);

        // Проверяем ответ
        assertThat(response.getError()).isEqualTo("Access denied");
    }
}
