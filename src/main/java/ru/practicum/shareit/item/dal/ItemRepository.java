package ru.practicum.shareit.item.dal;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item addItem(Long userId, Item item);

    Item updateItem(Long userId, Long itemId, Item item);

    Item getItemById(Long userId, Long itemId);

    List<Item> getAllItemsFromUser(Long userId);

    List<Item> searchItem(Long userId, String searchText);
}
