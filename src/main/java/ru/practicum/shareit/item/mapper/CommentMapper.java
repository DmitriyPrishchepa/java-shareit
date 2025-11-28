package ru.practicum.shareit.item.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Component
public class CommentMapper {

    private final ModelMapper modelMapper = new ModelMapper();

    public CommentDto mapToDto(Comment comment) {
        return modelMapper.map(comment, CommentDto.class);
    }

    public Comment mapFromDto(CommentDto commentDto) {
        return modelMapper.map(commentDto, Comment.class);
    }

    public List<CommentDto> mapToDto(List<Comment> comments) {
        return comments.stream().map(this::mapToDto).toList();
    }
}
