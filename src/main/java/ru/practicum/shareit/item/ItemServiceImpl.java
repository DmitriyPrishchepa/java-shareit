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
    public Item addItem(Long userId, ItemDto item) {
        return itemRepository.addItem(userId, itemMapper.mapFromDto(item));
    }

    @Override
    public Item updateItem(Long userId, Long itemId, ItemDto itemDto) {
        if (userId == null) {
            throw new MissingParameterException("userId was missing");
        }

        Item item = itemMapper.mapFromDto(itemDto);

        return itemRepository.updateItem(userId, itemId, item);
    }

    @Override
    public Item getItemById(Long userId, Long itemId) {
        return itemRepository.getItemById(userId, itemId);
    }

    @Override
    public List<Item> getAllItemsFromUser(Long userId) {
        return itemRepository.getAllItemsFromUser(userId);
    }

    @Override
    public List<Item> searchItem(Long userId, String searchText) {
        return itemRepository.searchItem(userId, searchText);
    }
}
