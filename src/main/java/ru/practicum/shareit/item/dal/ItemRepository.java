package ru.practicum.shareit.item.dal;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository {
    Item addItem(Long userId, Item item);

    Item updateItem(Long userId, Long itemId, Item item);

    Item getItemById(Long userId, Long itemId);

    List<Item> getAllItemsFromUser(Long userId);

    List<Item> searchItem(Long userId, String searchText);
}
