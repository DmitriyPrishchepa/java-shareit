package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoToReturn;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Component
public class CommentMapper {

    public CommentDto mapToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setText(comment.getText());
        dto.setItem(comment.getItem());
        dto.setAuthor(comment.getAuthor());
        dto.setCreated(comment.getCreated());
        return dto;
    }

    public Comment mapFromDto(CommentDto dto) {
        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setItem(dto.getItem());
        comment.setAuthor(dto.getAuthor());
        comment.setCreated(dto.getCreated());
        return comment;
    }

    public CommentDtoToReturn mapToReturnDto(Comment comment) {
        CommentDtoToReturn commentDto = new CommentDtoToReturn();
        commentDto.setId(comment.getId());
        String text = comment.getText();
        String result = text.substring(text.indexOf("\": \"") + 4, text.lastIndexOf("\""));
        commentDto.setText(result);
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }

    public List<CommentDto> mapToDto(List<Comment> comments) {
        return comments.stream().map(this::mapToDto).toList();
    }
}
