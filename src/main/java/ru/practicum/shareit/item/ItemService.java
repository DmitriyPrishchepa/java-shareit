package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemService {
    ItemDto addItem(Long userId, ItemDto item);

    ItemDto updateItem(Long userId, Long itemId, ItemDto item);

    ItemDto getItemById(Long userId, Long itemId);

    List<ItemDtoWithBookings> getAllItemsFromUser(Long userId);

    List<ItemDto> searchItem(Long userId, String searchText);

    CommentDto addComment(Long userId, Long itemId, String text);
}
