package ru.practicum.shareit.common;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.exceptions.ElementNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@Component
public class EntityUtils {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;


    public void checkExistingUser(Long userId) {
        try {
            userRepository.existsById(userId);
        } catch (EntityNotFoundException e) {
            throw new ElementNotFoundException("User not found");
        }
    }

    public void checkExistingItem(Long itemId) {
        try {
            itemRepository.existsById(itemId);
        } catch (EntityNotFoundException e) {
            throw new ElementNotFoundException("Item not found");
        }
    }
}
