package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody ItemDto item) {
        log.debug("userId {}", userId);
        return itemService.addItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @PathVariable("itemId") Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody ItemDto itemDto
    ) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable("itemId") Long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDtoWithBookings> getAllItemsFromUser(
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return itemService.getAllItemsFromUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam("text") String searchText) {
        return itemService.searchItem(userId, searchText);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto addComment(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable("itemId") Long itemId,
            @RequestBody String text
    ) {
        return itemService.addComment(userId, itemId, text);
    }
}
