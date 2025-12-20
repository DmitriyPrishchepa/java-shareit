package models;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentTest {
    @Test
    void testCommentProperties() {
        Item item = new Item();
        item.setId(10L);

        User author = new User();
        author.setId(20L);

        LocalDateTime created = LocalDateTime.of(2020, 12, 12, 12, 12, 12);

        Comment comment = new Comment();
        comment.setId(30L);
        comment.setText("This is a test comment");
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(created);

        assertEquals(30L, comment.getId());
        assertEquals("This is a test comment", comment.getText());
        assertEquals(item, comment.getItem());
        assertEquals(author, comment.getAuthor());
        assertEquals(created, comment.getCreated());
    }
}
