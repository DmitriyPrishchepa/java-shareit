package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.model.Request;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    @Transactional
    @Override
    public Request createRequest(long userId, String description) {
        Request request = new Request();
        request.setOwnerId(userId);
        request.setDescription(description);
        request.setCreated(LocalDateTime.now());

        Request saved = requestRepository.save(request);

        int startIndex = description.indexOf("\"description\": \"") + "\"description: \"".length() + 1;
        int endIndex = description.lastIndexOf("\"");

        String cleanedDescription = description.substring(startIndex, endIndex);

        saved.setDescription(cleanedDescription);

        return saved;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Request> getUserRequestsWithResponses(long userId) {
        return requestRepository.findAllByUserIdFetchResponses(userId);
    }

    @Override
    public Request getRequestById(long userId, long requestId) {
        return requestRepository.findById(requestId);
    }

    @Override
    public List<Request> getOtherUsersRequests(long userId) {
        PageRequest pageRequest = PageRequest.of(
                0,
                Integer.MAX_VALUE,
                Sort.by(Sort.Direction.DESC, "created")
        );
        return requestRepository.findAllByOwnerIdNot(userId, pageRequest).getContent();
    }
}
