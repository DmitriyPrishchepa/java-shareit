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
        NullPointerException exception = new NullPointerException("Element doesn't exist");

        ErrorResponse response = globalExceptionHandler.handleResourceReturnNull(exception);

        assertThat(response.getError()).isEqualTo("Element doesn't exist");
    }

    @Test
    void testHandleNotFound() {
        ElementNotFoundException exception = new ElementNotFoundException("Element not found");

        ErrorResponse response = globalExceptionHandler.handleNotFound(exception);

        assertThat(response.getError()).isEqualTo("Element not found");
    }

    @Test
    void testHandleValidation() {
        UserValidationException exception = new UserValidationException("Not valid data");

        ErrorResponse response = globalExceptionHandler.handleValidation(exception);

        assertThat(response.getError()).isEqualTo("Not valid data");
    }

    @Test
    void testHandleDuplicate() {
        DuplicateException exception = new DuplicateException("Duplicate");

        ErrorResponse response = globalExceptionHandler.handleDuplicate(exception);

        assertThat(response.getError()).isEqualTo("Duplicate");
    }

    @Test
    void testHandleMissingParameter() {
        MissingParameterException exception = new MissingParameterException("Missing parameter");

        ErrorResponse response = globalExceptionHandler.handelMissingParameter(exception);

        assertThat(response.getError()).isEqualTo("Missing parameter");
    }

    @Test
    void testManageUnhandledExceptions() {
        Exception exception = new Exception("Unhandled exception");

        ErrorResponse response = globalExceptionHandler.manageUnhandledExceptions(exception);

        assertThat(response.getError()).isEqualTo("Unhandled exception");
    }

    @Test
    void testHandleBookingValidation() {
        BookingValidationException exception = new BookingValidationException("Booking Validation exception");

        ErrorResponse response = globalExceptionHandler.handleBookingValidation(exception);

        assertThat(response.getError()).isEqualTo("Booking Validation exception");
    }

    @Test
    void testHandeWrongUser() {
        WrongUserException exception = new WrongUserException("Wrong user");

        ErrorResponse response = globalExceptionHandler.handeWrongUser(exception);

        assertThat(response.getError()).isEqualTo("Wrong user");
    }

    @Test
    void testHandeAccessDenied() {
        AccessToCommentDeniedException exception = new AccessToCommentDeniedException("Access denied");

        ErrorResponse response = globalExceptionHandler.handeAccessDenied(exception);

        assertThat(response.getError()).isEqualTo("Access denied");
    }
}
