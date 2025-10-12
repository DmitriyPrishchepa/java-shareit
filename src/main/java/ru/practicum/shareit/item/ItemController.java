package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public Item addItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody ItemDto item) {
        return itemService.addItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(
            @PathVariable("itemId") Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody ItemDto itemDto
    ) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public Item getItemById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable("itemId") Long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    public List<Item> getAllItemsFromUser(
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return itemService.getAllItemsFromUser(userId);
    }

    @GetMapping("/search")
    public List<Item> searchItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam("text") String searchText) {
        return itemService.searchItem(userId, searchText);
    }
}
