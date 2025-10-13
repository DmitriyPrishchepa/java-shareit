package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.exceptions.MissingParameterException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto addItem(Long userId, ItemDto item) {
        Item returnedItem = itemRepository.addItem(userId, mapFromDto(item));
        return mapToDto(returnedItem);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        if (userId == null) {
            throw new MissingParameterException("userId was missing");
        }

        Item returnedItem = itemRepository.updateItem(userId, itemId, mapFromDto(itemDto));
        return mapToDto(returnedItem);
    }

    @Override
    public ItemDto getItemById(Long userId, Long itemId) {
        Item returnedItem = itemRepository.getItemById(userId, itemId);
        return mapToDto(returnedItem);
    }

    @Override
    public List<ItemDto> getAllItemsFromUser(Long userId) {
        return itemRepository.getAllItemsFromUser(userId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<ItemDto> searchItem(Long userId, String searchText) {
        return itemRepository.searchItem(userId, searchText)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private Item mapFromDto(ItemDto itemDto) {
        return itemMapper.mapFromDto(itemDto);
    }

    private ItemDto mapToDto(Item item) {
        return itemMapper.mapToDto(item);
    }
}
