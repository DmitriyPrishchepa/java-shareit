package json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.CommentDtoToReturn;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {CommentMapper.class})
public class CommentMapperTests {

    @Autowired
    private CommentMapper commentMapper;

    @Test
    void mapToReturnDto() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("\\\"text\\\": \\\"Пример комментария для обработки\\\"");
        comment.setCreated(LocalDateTime.now());
        User author = new User();
        author.setName("Имя автора");
        comment.setAuthor(author);

        CommentDtoToReturn commentDto = mapToReturnDto(comment);

        assertEquals(comment.getId(), commentDto.getId());

        assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName());
        assertEquals(comment.getCreated(), commentDto.getCreated());
    }

    private CommentDtoToReturn mapToReturnDto(Comment comment) {
        CommentDtoToReturn commentDto = new CommentDtoToReturn();
        commentDto.setId(comment.getId());

        String text = comment.getText();
        if (text.contains("\": \"")) {
            int startIndex = text.indexOf("\": \"") + 4;
            int endIndex = text.lastIndexOf("\"");
            if (startIndex < endIndex) {
                String result = text.substring(startIndex, endIndex);
                commentDto.setText(result);
            } else {
                commentDto.setText("");
            }
        } else {
            commentDto.setText("");
        }

        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreated(comment.getCreated());

        return commentDto;
    }
}
