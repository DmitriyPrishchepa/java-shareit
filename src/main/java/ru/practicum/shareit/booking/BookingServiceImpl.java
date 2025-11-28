package ru.practicum.shareit.booking;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoToReturnValue;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.util.BookingState;
import ru.practicum.shareit.booking.util.BookingStateSearchParams;
import ru.practicum.shareit.booking.util.BookingValidator;
import ru.practicum.shareit.common.EntityUtils;
import ru.practicum.shareit.exception.exceptions.BookingValidationException;
import ru.practicum.shareit.exception.exceptions.ElementNotFoundException;
import ru.practicum.shareit.exception.exceptions.MissingParameterException;
import ru.practicum.shareit.exception.exceptions.WrongUserException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;
    private final EntityUtils entityUtils;

    @Override
    public BookingDtoToReturnValue createBooking(Long userId, BookingDto bookingDto) {
        if (!userRepository.existsById(userId)) {
            throw new ElementNotFoundException("User not found");
        }

        if (!itemRepository.existsById(bookingDto.getItemId())) {
            throw new ElementNotFoundException("Item not found");
        }

        Item item = itemRepository.getReferenceById(bookingDto.getItemId());

        if (!item.getAvailable()) {
            throw new BookingValidationException("Item not available");
        }

        User booker = userRepository.getReferenceById(userId);

        BookingValidator.validateBooking(bookingDto);

        Booking booking = bookingMapper.mapFromDto(bookingDto);
        booking.setItem(item); // Устанавливаем связь с Item
        booking.setBooker(booker); // Устанавливаем связь с User

        Booking bookingSaved = bookingRepository.save(booking);

        BookingDtoToReturnValue returnedDto = new BookingDtoToReturnValue();
        returnedDto.setItem(bookingSaved.getItem());
        returnedDto.setBooker(bookingSaved.getBooker());
        returnedDto.setStart(bookingSaved.getStart());
        returnedDto.setEnd(bookingSaved.getEnd());
        returnedDto.setStatus(BookingState.WAITING);

        return returnedDto;
    }

    @Override
    public BookingDto updateBookingApproval(Long userId, Long bookingId, Boolean approved) {

        if (bookingId == null) {
            throw new MissingParameterException("booking id is missing");
        }

        Booking booking = checkExitstingBooking(bookingId);

        if (!booking.getBooker().getId().equals(userId)) {
            throw new ElementNotFoundException("Wrong user");
        }

        if (approved) {
            booking.setStatus(BookingState.APPROVED);
        } else {
            booking.setStatus(BookingState.REJECTED);
        }

        Booking updatedBooking = bookingRepository.save(booking);
        return bookingMapper.mapToDto(updatedBooking);
    }

    @Override
    public BookingDto findBookingById(Long userId, Long bookingId) {
        entityUtils.checkExistingUser(userId);
        Booking booking = checkExitstingBooking(bookingId);

        Long ownerId = booking.getItem().getOwnerId();
        Long bookerId = booking.getBooker().getId();

        if (userId.equals(ownerId) || userId.equals(bookerId)) {
            return bookingMapper.mapToDto(booking);
        } else {
            throw new WrongUserException("Only owner or booker can get information about booking");
        }
    }

    @Override
    public List<BookingDto> findByBookerIdAndStateSorted(Long bookerId, BookingStateSearchParams state) {

        List<Booking> bookings;

        return switch (state) {
            case PAST -> {
                bookings = bookingRepository.findPastBookingsByBookerId(bookerId, LocalDateTime.now());
                yield mapToListDto(bookings);
            }
            case FUTURE -> {
                bookings = bookingRepository.findFutureBookingsByBookerId(bookerId, LocalDateTime.now());
                yield mapToListDto(bookings);
            }
            case CURRENT -> {
                bookings = bookingRepository.findCurrentBookingsByBookerId(bookerId, LocalDateTime.now());
                yield mapToListDto(bookings);
            }

            case WAITING -> {
                bookings = bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingStateSearchParams.WAITING);
                yield mapToListDto(bookings);
            }

            case REJECTED -> {
                bookings = bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingStateSearchParams.REJECTED);
                yield mapToListDto(bookings);
            }

            default -> {
                bookings = bookingRepository.findAllByItemOwnerId(bookerId);
                yield mapToListDto(bookings);
            }
        };
    }

    @Override
    public List<BookingDto> findAllByBookingItemOwnerIdAndStatus(Long ownerId, BookingStateSearchParams state) {
        List<Booking> bookings;

        return switch (state) {
            case PAST -> {
                bookings = bookingRepository.findPastBookingsByOwnerId(ownerId, LocalDateTime.now());
                yield mapToListDto(bookings);
            }
            case FUTURE -> {
                bookings = bookingRepository.findFutureBookingsByOwnerId(ownerId, LocalDateTime.now());
                yield mapToListDto(bookings);
            }
            case CURRENT -> {
                bookings = bookingRepository.findCurrentBookingsByOwnerId(ownerId, LocalDateTime.now());
                yield mapToListDto(bookings);
            }

            case WAITING -> {
                bookings = bookingRepository.findAllByOwnerIdAndStatus(ownerId, BookingStateSearchParams.WAITING);
                yield mapToListDto(bookings);
            }

            case REJECTED -> {
                bookings = bookingRepository.findAllByOwnerIdAndStatus(ownerId, BookingStateSearchParams.REJECTED);
                yield mapToListDto(bookings);
            }

            default -> {
                bookings = bookingRepository.findAllByItemOwnerId(ownerId);
                yield mapToListDto(bookings);
            }
        };
    }

    @Override
    public List<BookingDto> findAllByBookerId(Long bookerId) {
        List<Booking> bookings = bookingRepository.findAllByBookerId(bookerId);
        return mapToListDto(bookings);
    }

    @Override
    public List<BookingDto> findAllByItemOwnerId(Long ownerId) {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerId(ownerId);
        return mapToListDto(bookings);
    }

    private List<BookingDto> mapToListDto(List<Booking> bookings) {
        return bookings.stream().map(bookingMapper::mapToDto).toList();
    }

    private Booking checkExitstingBooking(Long bookingId) {
        try {
            return bookingRepository.getReferenceById(bookingId);
        } catch (EntityNotFoundException e) {
            throw new ElementNotFoundException("Booking not found");
        }
    }
}
