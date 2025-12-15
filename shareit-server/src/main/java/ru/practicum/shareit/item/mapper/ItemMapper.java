package ru.practicum.shareit.item.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {

    private final ModelMapper modelMapper = new ModelMapper();

    public ItemDto mapToDto(Item item) {
        return this.modelMapper.map(item, ItemDto.class);
    }

    public Item mapFromDto(ItemDto itemDto) {
        return this.modelMapper.map(itemDto, Item.class);
    }
}
