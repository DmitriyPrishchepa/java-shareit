package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDtoToReturn;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.dto.ItemWithCommentsToReturn;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Long userId, ItemDto item);

    ItemDto updateItem(Long userId, Long itemId, ItemDto item);

    ItemWithCommentsToReturn getItemById(Long userId, Long itemId);

    List<ItemDtoWithBookings> getAllItemsFromUser(Long userId);

    List<ItemDto> searchItem(Long userId, String searchText);

    CommentDtoToReturn addComment(Long userId, Long itemId, String text);
}
