package validators;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.exceptions.MissingParameterException;
import ru.practicum.shareit.exception.exceptions.UserValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.util.UserValidator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserValidatorTest {

    @Test
    void testValidateUser_Success() {
        User user = new User();
        user.setEmail("test@example.com");

        assertDoesNotThrow(() -> UserValidator.validateUser(user));
    }

    @Test
    void testValidateUser_MissingEmail() {
        User user = new User();

        assertThrows(MissingParameterException.class, () -> UserValidator.validateUser(user),
                "Email cannot be null");
    }

    @Test
    void testValidateUser_InvalidEmail() {
        User user = new User();
        user.setEmail("invalid_email");

        assertThrows(UserValidationException.class, () -> UserValidator.validateUser(user),
                "Email is invalid");
    }
}
