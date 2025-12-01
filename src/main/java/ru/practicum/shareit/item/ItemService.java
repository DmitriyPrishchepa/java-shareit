package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Long userId, ItemDto item);

    ItemDto updateItem(Long userId, Long itemId, ItemDto item);

    ItemDto getItemById(Long userId, Long itemId);

    List<ItemDto> getAllItemsFromUser(Long userId);

    List<ItemDto> searchItem(Long userId, String searchText);
}
