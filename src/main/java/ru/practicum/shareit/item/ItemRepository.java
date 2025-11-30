package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Override
    <S extends Item> S save(S entity);

    @Override
    Item getReferenceById(Long aLong);

    @Override
    void deleteById(Long aLong);

    Page<Item> findAllByOwnerId(Long userId, Pageable page);

    @Query("SELECT i " +
            "from Item i " +
            "WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) ")
    List<Item> findItemsByNameContaining(@Param("text") String text);

    boolean existsById(Long id);
}
