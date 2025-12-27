package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(
            "select c " +
                    "from Comment c " +
                    "where c.item.id = :itemId " +
                    "and c.item.ownerId = :userId"
    )
    List<Comment> findAllByItemAndUserId(@Param("userId") Long userId, @Param("itemId") Long itemId);
}
