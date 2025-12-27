package ru.practicum.shareit.request;

import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestService {
    Request createRequest(long userId, String description);

    List<Request> getUserRequestsWithResponses(long userId);

    Request getRequestById(long userId, long requestId);

    List<Request> getOtherUsersRequests(long userId);
}
