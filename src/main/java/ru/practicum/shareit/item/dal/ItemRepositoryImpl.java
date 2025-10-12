package ru.practicum.shareit.item.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.util.ItemValidation;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {

    private final UserRepository userRepository;

    private final AtomicLong itemIdGenerator = new AtomicLong(0);

    @Autowired
    public ItemRepositoryImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Item addItem(Long userId, Item item) {
        ItemValidation.validateItemFields(item);
        long generatedId = generateItemId();
        //добавляем в список вещей определенного юзера
        User user = userRepository.getUserById(userId);
        item.setOwnerId(userId);
        item.setId(generatedId);
        //добавляем в список вещей юзера
        user.getUserItems().put(generatedId, item);

        return getItemById(user.getId(), generatedId);
    }

    @Override
    public Item updateItem(Long userId, Long itemId, Item item) {

        //заметка: пересмотреть на itemDto

        //проверяем существуует ли юзер
        User user = userRepository.getUserById(userId);

        //находим нужный item
        Item existedItem = getItemById(user.getId(), itemId);

        existedItem.setName(item.getName());
        existedItem.setDescription(item.getDescription());
        existedItem.setAvailable(item.getAvailable());

        //обновляем в списке вещей юзера
        user.getUserItems().put(item.getId(), existedItem);

        return getItemById(user.getId(), existedItem.getId());
    }

    @Override
    public Item getItemById(Long userId, Long itemId) {
        User user = userRepository.getUserById(userId);
        return user.getUserItems().get(itemId);
    }

    @Override
    public List<Item> getAllItemsFromUser(Long userId) {
        User user = userRepository.getUserById(userId);
        return new ArrayList<>(user.getUserItems().values());
    }

    @Override
    public List<Item> searchItem(Long userId, String searchText) {

        if (searchText.isEmpty()) {
            return new ArrayList<>();
        }

        User user = userRepository.getUserById(userId);
        Map<Long, Item> items = user.getUserItems();

        return items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(searchText.toLowerCase()))
                .filter(item -> !item.getAvailable().equals("false"))
                .toList();
    }

    public long generateItemId() {
        return itemIdGenerator.getAndIncrement();
    }
}
