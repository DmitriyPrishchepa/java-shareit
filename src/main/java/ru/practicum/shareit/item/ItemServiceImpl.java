package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.common.EntityUtils;
import ru.practicum.shareit.exception.exceptions.AccessToCommentDeniedException;
import ru.practicum.shareit.exception.exceptions.ElementNotFoundException;
import ru.practicum.shareit.exception.exceptions.MissingParameterException;
import ru.practicum.shareit.exception.exceptions.WrongUserException;
import ru.practicum.shareit.item.dto.CommentDtoToReturn;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.dto.ItemWithCommentsToReturn;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.util.ItemValidator;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final UserServiceImpl userServiceImpl;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;
    private final EntityUtils entityUtils;

    @Transactional
    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {

        if (userId == null) {
            throw new MissingParameterException("X-Sharer-User-Id header required");
        }

        if (!userRepository.existsById(userId)) {
            throw new ElementNotFoundException("User not found");
        }

        Item item = itemMapper.mapFromDto(itemDto);

        ItemValidator.validateItemFields(item);

        item.setOwnerId(userId);

        Item itemSaved = itemRepository.save(item);
        return itemMapper.mapToDto(itemSaved);
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        if (userId == null) {
            throw new MissingParameterException("userId was missing");
        }

        if (!userRepository.existsById(userId)) {
            throw new ElementNotFoundException("User not found");
        }

        itemRepository.existsById(itemId);
        Item existingItem = itemRepository.getReferenceById(itemId);
        Item updatedItem = ItemValidator.validateAndUpdateItemFields(existingItem, itemMapper.mapFromDto(itemDto));

        Item itemSaved = itemRepository.save(updatedItem);

        return itemMapper.mapToDto(itemSaved);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemWithCommentsToReturn getItemById(Long userId, Long itemId) {

        entityUtils.checkExistingUser(userId);
        entityUtils.checkExistingItem(itemId);

        Item item = itemRepository.getReferenceById(itemId);
        List<Comment> comments = commentRepository.findAllByItemAndUserId(item.getOwnerId(), itemId);
        ItemWithCommentsToReturn dto = new ItemWithCommentsToReturn();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setOwnerId(item.getOwnerId());
        dto.setAvailable(item.getAvailable());
        dto.setLastBooking(null);
        dto.setNextBooking(null);
        dto.setComments(comments);
        return dto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDtoWithBookings> getAllItemsFromUser(Long userId) {
        PageRequest pageRequest = PageRequest.of(0, Integer.MAX_VALUE);

        List<ItemDto> itemsOfUser = itemRepository.findAllByOwnerId(userId, pageRequest)
                .getContent().stream().map(itemMapper::mapToDto).toList();

        return itemsOfUser.stream()
                .map(itemDto -> getItemWithBookings(itemDto.getId()))
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> searchItem(Long userId, String searchText) {

        List<ItemDto> itemsFound = itemRepository.findItemsByNameContaining(searchText)
                .stream()
                .filter(Item::getAvailable) // Фильтруем, оставляя только те элементы, где available = true
                .map(itemMapper::mapToDto)
                .toList();

        if (searchText.isEmpty()) {
            return new ArrayList<>();
        }

        return itemsFound;
    }

    @Transactional
    public CommentDtoToReturn addComment(Long userId, Long itemId, String text) {

        User user = userRepository.getReferenceById(userId);

        Item item = itemRepository.getReferenceById(itemId);

        LocalDateTime dateTime = LocalDateTime.now();

        Booking booking = bookingRepository.findByItemId(itemId);

        if (!(Objects.equals(booking.getItem().getId(), item.getId()) &&
                Objects.equals(booking.getBooker().getId(), user.getId()))) {
            throw new WrongUserException("You cannot leave a comment");
        }

        if (booking.getEnd().isAfter(LocalDateTime.now())) {
            throw new AccessToCommentDeniedException("Rental date is not over yet");
        }

        Comment comment = new Comment();
        comment.setText(text.trim());
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(dateTime);

        Comment commentSaved = commentRepository.save(comment);

        return commentMapper.mapToReturnDto(commentSaved);
    }

    public ItemDtoWithBookings getItemWithBookings(Long itemId) {

        itemRepository.existsById(itemId);

        Item item = itemRepository.getReferenceById(itemId);
        ItemDto itemDto = itemMapper.mapToDto(item);

        ItemDtoWithBookings itemDtoWithBookings = new ItemDtoWithBookings();
        itemDtoWithBookings.setId(itemDto.getId());
        itemDtoWithBookings.setName(itemDto.getName());
        itemDtoWithBookings.setDescription(itemDto.getDescription());
        itemDtoWithBookings.setAvailable(itemDto.getAvailable());

        List<Booking> bookings = bookingRepository.findAllByItemId(itemId);
        List<BookingDto> bookingsDto = bookingMapper.mapToDto(bookings);
        itemDtoWithBookings.setBookings(bookingsDto);

        return itemDtoWithBookings;
    }
}
