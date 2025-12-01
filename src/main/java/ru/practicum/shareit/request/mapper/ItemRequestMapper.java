package ru.practicum.shareit.request.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Component
public class ItemRequestMapper {

    private final ModelMapper modelMapper = new ModelMapper();

    public ItemRequestDto mapToDto(ItemRequest itemRequest) {
        return this.modelMapper.map(itemRequest, ItemRequestDto.class);
    }

    public ItemRequest mapFromDto(ItemRequestDto itemRequestDto) {
        return this.modelMapper.map(itemRequestDto, ItemRequest.class);
    }
}
