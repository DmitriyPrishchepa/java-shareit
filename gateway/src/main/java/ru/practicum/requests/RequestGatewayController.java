package ru.practicum.requests;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Slf4j
public class RequestGatewayController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(
            @RequestHeader("X-Sharer-User-Id") @Positive long userId,
            @RequestBody String description
    ) {
        log.info("Request with description {} created to user with userId {}", description, userId);
        return requestClient.addRequest(userId, description);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequestsWithResponses(
            @RequestHeader("X-Sharer-User-Id") @Positive long userId
    ) {
        log.info("Find requests of user with userId {}", userId);
        return requestClient.getRequestsWithResponse(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherUsersRequests(
            @RequestHeader("X-Sharer-User-Id") @Positive long userId
    ) {
        log.info("Find all requests of users, not for user with userId {}", userId);
        return requestClient.getOtherUsersRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(
            @RequestHeader("X-Sharer-User-Id") @Positive long userId,
            @PathVariable("requestId") @Positive long requestId
    ) {
        log.info("Find requests of user with userId {} and requestId {}", userId, requestId);
        return requestClient.getRequestById(userId, requestId);
    }
}
