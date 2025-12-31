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
import ru.practicum.shareit.booking.util.BookingState;
import ru.practicum.shareit.exception.exceptions.BookingValidationException;
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
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

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

    //интеграционное тестирование
    @Transactional(readOnly = true)
    @Override
    public List<ItemDtoWithBookings> getAllItemsFromUser(Long userId) {
        PageRequest pageRequest = PageRequest.of(0, Integer.MAX_VALUE);

        List<ItemDto> itemsOfUser = itemRepository.findAllByOwnerId(userId, pageRequest)
                .getContent().stream().map(itemMapper::mapToDto).toList();

        // Загружаем все бронирования для всех элементов пользователя
        List<Long> itemsIds = itemsOfUser.stream().map(ItemDto::getId).toList();
        List<Booking> allBookingsForAllItems = bookingRepository.findAllByItemIdIn(itemsIds);

        // Группируем бронирования по идентификаторам элементов
        Map<Long, List<Booking>> bookingsByItemId = allBookingsForAllItems.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        return itemsOfUser.stream()
                .map(itemDto -> {
                    ItemDtoWithBookings itemDtoWithBookings = new ItemDtoWithBookings();
                    itemDtoWithBookings.setId(itemDto.getId());
                    itemDtoWithBookings.setName(itemDto.getName());
                    itemDtoWithBookings.setDescription(itemDto.getDescription());
                    itemDtoWithBookings.setAvailable(itemDto.getAvailable());

                    List<BookingDto> bookingsDto = bookingsByItemId.getOrDefault(
                                    itemDto.getId(), Collections.emptyList()
                            )
                            .stream()
                            .map(bookingMapper::mapToDto)
                            .toList();

                    itemDtoWithBookings.setBookings(bookingsDto);

                    return itemDtoWithBookings;
                }).toList();
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

    //интеграционное тестирование
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

        if (booking.getStatus().equals(BookingState.APPROVED)) {
            throw new BookingValidationException("ex");
        }

        Comment comment = new Comment();
        comment.setText(text.trim());
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(dateTime);

        Comment commentSaved = commentRepository.save(comment);
        return commentMapper.mapToReturnDto(commentSaved);
    }
}
