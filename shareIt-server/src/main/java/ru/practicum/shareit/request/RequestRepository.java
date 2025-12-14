package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    @Query(
            "select r " +
                    "from Request r " +
                    "LEFT JOIN FETCH r.responses " +
                    "WHERE r.ownerId = :userId " +
                    "ORDER BY r.created ASC"
    )
    List<Request> findAllByUserIdFetchResponses(@Param("userId") long userId);

    @Query(
            "select r " +
                    "from Request r " +
                    "LEFT JOIN FETCH r.responses " +
                    "where r.id = :requestId"
    )
    Request findById(@Param("requestId") long requestId);

    Page<Request> findAllByOwnerIdNot(long userId, PageRequest pageRequest);
}
