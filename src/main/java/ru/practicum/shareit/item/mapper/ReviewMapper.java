package ru.practicum.shareit.item.mapper;

import org.modelmapper.ModelMapper;
import ru.practicum.shareit.item.dto.ReviewDto;
import ru.practicum.shareit.item.model.Review;

public class ReviewMapper {
    private final ModelMapper modelMapper = new ModelMapper();

    public ReviewDto mapToDto() {
        return this.modelMapper.map(Review.class, ReviewDto.class);
    }

    public Review mapFromDto() {
        return this.modelMapper.map(ReviewDto.class, Review.class);
    }
}
