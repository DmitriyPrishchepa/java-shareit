package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoToReturn;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.dto.ItemWithCommentsToReturn;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody ItemDto item) {
        log.debug("userId {}", userId);
        return itemService.addItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @Positive @PathVariable("itemId") Long itemId,
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody ItemDto itemDto
    ) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemWithCommentsToReturn getItemById(
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
            @Positive @PathVariable("itemId") Long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDtoWithBookings> getAllItemsFromUser(
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return itemService.getAllItemsFromUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam("text") String searchText) {
        return itemService.searchItem(userId, searchText);
    }

    @PostMapping("{itemId}/comment")
    public CommentDtoToReturn addComment(
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
            @Positive @PathVariable("itemId") Long itemId,
            @RequestBody String text
    ) {
        return itemService.addComment(userId, itemId, text);
    }
}
