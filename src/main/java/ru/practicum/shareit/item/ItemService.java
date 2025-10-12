package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Service
public interface ItemService {
    Item addItem(Long userId, ItemDto item);

    Item updateItem(Long userId, Long itemId, ItemDto item);

    Item getItemById(Long userId, Long itemId);

    List<Item> getAllItemsFromUser(Long userId);

    List<Item> searchItem(Long userId, String searchText);
}
