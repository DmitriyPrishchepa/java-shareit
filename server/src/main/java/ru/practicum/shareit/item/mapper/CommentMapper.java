package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDtoToReturn;
import ru.practicum.shareit.item.model.Comment;

@Component
public class CommentMapper {
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
}
