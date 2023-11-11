package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.EntityValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public Booking create(Long userId, BookingDto dto) {
        checkIfUserOrItemOrBookingExists(userId, dto.getItemId(), null);
        Item item = itemRepository.getReferenceById(dto.getItemId());
        checkIfItemIsAvailable(item.getId());
        checkIfItemBelongsToUser(dto.getItemId(), userId);
        Booking booking = Booking.builder()
                .start(dto.getStart())
                .end(dto.getEnd())
                .item(itemRepository.getReferenceById(dto.getItemId()))
                .booker(userRepository.getReferenceById(userId))
                .status(Status.WAITING)
                .build();
        return bookingRepository.save(booking);
    }

    @Override
    public Booking approve(Long userId, Long bookingId, boolean approved) {
        checkIfUserOrItemOrBookingExists(userId, null, bookingId);
        Booking booking = bookingRepository.getReferenceById(bookingId);
        checkIfItemIsAvailable(booking.getItem().getId());
        checkIfItemBelongsToUser(booking.getItem().getId(), booking.getBooker().getId());
        if (userId != booking.getItem().getOwner().getId()) {
            throw new EntityValidationException("Только владелец вещи может подтверждать бронь!");
        } else {
            //approved ? booking.setStatus(Status.APPROVED) : booking.setStatus(Status.REJECTED);
            if (approved) {
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(Status.REJECTED);
            }
        }
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBookingInfo(Long userId, Long bookingId) {
        checkIfUserOrItemOrBookingExists(userId, null, bookingId);
        Booking booking = bookingRepository.getReferenceById(bookingId);
        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return booking;
        }
        throw new EntityNotFoundException("Просматривать могут только владелец вещи, либо арендатор!");
    }

    @Override
    public List<Booking> getUserBookings(Long userId, State state) {
        checkIfUserOrItemOrBookingExists(userId, null, null);
        switch (state) {
            case ALL:
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
            case CURRENT:
                return bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now());
            case PAST:
                return bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
            case FUTURE:
                return bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
            case WAITING:
                return bookingRepository.findAllByBookerIdAndItemStatusOrderByStartDesc(userId, Status.WAITING);
            case REJECTED:
                return bookingRepository.findAllByBookerIdAndItemStatusOrderByStartDesc(userId, Status.REJECTED);
            default:
                throw new EntityNotFoundException("Unknown state");
        }
    }

    @Override
    public List<Booking> getBookingsByOwner(Long userId, State state) {
        checkIfUserOrItemOrBookingExists(userId, null, null);
        switch (state) {
            case ALL:
                return bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);

            case CURRENT:
                return bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now());

            case PAST:
                return bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());

            case FUTURE:
                return bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());

            case WAITING:
                return bookingRepository.findAllByItemOwnerIdAndItemStatusOrderByStartDesc(userId, Status.WAITING);

            case REJECTED:
                return bookingRepository.findAllByItemOwnerIdAndItemStatusOrderByStartDesc(userId, Status.REJECTED);

            default:
                throw new EntityNotFoundException("Unknown state");
        }
    }

    private void checkIfUserOrItemOrBookingExists(Long userId, Long itemId, Long bookingId) {
        if (userId != null && !userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Пользователь не найден!");
        }
        if (itemId != null && !itemRepository.existsById(itemId)) {
            throw new EntityNotFoundException("Вещь не найдена!");
        }
        if (bookingId != null && !bookingRepository.existsById(bookingId)) {
            throw new EntityNotFoundException("Бронь не найдена!");
        }
    }

    private void checkIfItemBelongsToUser(Long itemId, Long userId) {
        Item item = itemRepository.getReferenceById(itemId);
        if (item.getOwner().getId() == userId) {
            throw new EntityValidationException("Пользователь не может бронировать собственную вещь!");
        }
    }

    private void checkIfItemIsAvailable(Long itemId) {
        Item item = itemRepository.getReferenceById(itemId);
        if (!item.getIsAvailable()) {
            throw new EntityValidationException("Вещь недоступна к бронированию!");
        }
    }
}
