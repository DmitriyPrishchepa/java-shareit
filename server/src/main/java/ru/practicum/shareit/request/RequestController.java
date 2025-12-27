package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Slf4j
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public Request createRequest(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody String description
    ) {
        return requestService.createRequest(userId, description);
    }

    @GetMapping
    public List<Request> getUserRequestsWithResponses(
            @RequestHeader("X-Sharer-User-Id") long userId
    ) {
        log.debug("userId {}", userId);
        return requestService.getUserRequestsWithResponses(userId);
    }

    @GetMapping("/all")
    public List<Request> getOtherUsersRequests(
            @RequestHeader("X-Sharer-User-Id") long userId
    ) {
        return requestService.getOtherUsersRequests(userId);
    }

    @GetMapping("/{requestId}")
    public Request getRequestById(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable("requestId") long requestId
    ) {
        return requestService.getRequestById(userId, requestId);
    }
}
