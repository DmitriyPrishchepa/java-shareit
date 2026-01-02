package ru.practicum.items;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exception.exceptions.MissingParameterException;
import ru.practicum.items.dto.ItemDto;
import ru.practicum.items.validation.ItemValidator;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemGatewayController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(
            @RequestHeader("X-Sharer-User-Id") @Positive Long userId,
            @RequestBody ItemDto item) {
        if (userId == null) {
            throw new MissingParameterException("X-Sharer-User-Id header required");
        }
        ItemValidator.validateItemFields(item);
        log.info("Item created is {}", item);
        return itemClient.addItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @PathVariable("itemId") @Positive Long itemId,
            @RequestHeader("X-Sharer-User-Id") @Positive Long userId,
            @RequestBody ItemDto itemDto
    ) {
        if (userId == null) {
            throw new MissingParameterException("userId was missing");
        }
        log.info("Updated item looks like {}", itemDto);
        return itemClient.updateItem(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @RequestHeader("X-Sharer-User-Id") @Positive Long userId,
            @PathVariable("itemId") @Positive Long itemId) {
        log.info("Found item with userId {} and itemId {}", userId, itemId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsFromUser(
            @RequestHeader("X-Sharer-User-Id") @Positive Long userId
    ) {
        log.info("Get all items with userId {}", userId);
        return itemClient.getAllItemsFromUser(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(
            @RequestHeader("X-Sharer-User-Id") @Positive Long userId,
            @RequestParam("text") String searchText) {
        log.info("Search items with userId {}, searchText {}", userId, searchText);
        return itemClient.search(userId, searchText);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader("X-Sharer-User-Id") @Positive Long userId,
            @PathVariable("itemId") @Positive Long itemId,
            @RequestBody String text
    ) {
        log.info("Add comment to item with id {}, userId {} and description {}", itemId, userId, text);
        return itemClient.addComment(userId, itemId, text);
    }
}
