package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDtoBookingMapper;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.EntityValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingDtoBookingMapper mapper;

    @Override
    public BookingDto create(Long userId, InputBookingDto dto) {
        if (dto.getStart().equals(dto.getEnd()) || dto.getStart().isAfter(dto.getEnd())) {
            throw new EntityValidationException("Некорректные начало и конец!");
        }

        User booker = getUser(userId);
        Item item = getItem(dto.getItemId());

        if (!item.getAvailable()) {
            throw new EntityValidationException("Вещь недоступна к бронированию!");
        }

        if (Objects.equals(item.getOwner().getId(), userId)) {
            throw new EntityNotFoundException("Владелец не может бронировать собственную вещь!");
        }

        Booking booking = mapper.mapInputBookingDtoToBooking(dto);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        return mapper.mapBookingToBookingDto(bookingRepository.saveAndFlush(booking));
    }

    @Override
    public BookingDto approve(Long userId, Long bookingId, boolean approved) {
        getUser(userId);
        Booking booking = getBooking(bookingId);

        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new EntityValidationException("Бронь уже подтверждена!");
        }

        if (!booking.getItem().getAvailable()) {
            throw new EntityValidationException("Вещь недоступна к бронированию!");
        }

        if (!Objects.equals(userId, booking.getItem().getOwner().getId())) {
            throw new EntityNotFoundException("Только владелец вещи может подтверждать бронь!");
        } else {
            if (approved) {
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(Status.REJECTED);
            }
        }
        return mapper.mapBookingToBookingDto(bookingRepository.saveAndFlush(booking));
    }

    @Override
    public BookingDto getBookingInfo(Long userId, Long bookingId) {
        getUser(userId);
        Booking booking = getBooking(bookingId);

        if (Objects.equals(booking.getBooker().getId(), userId) ||
                Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            return mapper.mapBookingToBookingDto(booking);
        }

        throw new EntityNotFoundException("Просматривать могут только владелец вещи, либо арендатор!");
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, String state, Integer from, Integer size) {
        State.checkState(state);
        getUser(userId);
        LocalDateTime now = LocalDateTime.now();
        Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));

        switch (State.valueOf(state)) {
            case ALL:
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId, page)
                        .stream()
                        .map(mapper::mapBookingToBookingDto)
                        .collect(Collectors.toUnmodifiableList());

            case CURRENT:
                return bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartAsc(userId,
                                now, now, page)
                        .stream()
                        .map(mapper::mapBookingToBookingDto)
                        .collect(Collectors.toUnmodifiableList());

            case PAST:
                return bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, now, page)
                        .stream()
                        .map(mapper::mapBookingToBookingDto)
                        .collect(Collectors.toUnmodifiableList());

            case FUTURE:
                return bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, now, page)
                        .stream()
                        .map(mapper::mapBookingToBookingDto)
                        .collect(Collectors.toUnmodifiableList());

            case WAITING:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING, page)
                        .stream()
                        .map(mapper::mapBookingToBookingDto)
                        .collect(Collectors.toUnmodifiableList());

            case REJECTED:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, page)
                        .stream()
                        .map(mapper::mapBookingToBookingDto)
                        .collect(Collectors.toUnmodifiableList());

            default:
                throw new EntityValidationException("Непредвиденная ошибка! Обратитесь в службу поддержки!");
        }
    }

    @Override
    public List<BookingDto> getBookingsByOwner(Long userId, String state, Integer from, Integer size) {
        State.checkState(state);
        getUser(userId);
        LocalDateTime now = LocalDateTime.now();
        Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));

        switch (State.valueOf(state)) {
            case ALL:
                return bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId, page)
                        .stream()
                        .map(mapper::mapBookingToBookingDto)
                        .collect(Collectors.toUnmodifiableList());

            case CURRENT:
                return bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                                now, now, page)
                        .stream()
                        .map(mapper::mapBookingToBookingDto)
                        .collect(Collectors.toUnmodifiableList());

            case PAST:
                return bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(userId, now, page)
                        .stream()
                        .map(mapper::mapBookingToBookingDto)
                        .collect(Collectors.toUnmodifiableList());

            case FUTURE:
                return bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId, now, page)
                        .stream()
                        .map(mapper::mapBookingToBookingDto)
                        .collect(Collectors.toUnmodifiableList());

            case WAITING:
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING, page)
                        .stream()
                        .map(mapper::mapBookingToBookingDto)
                        .collect(Collectors.toUnmodifiableList());

            case REJECTED:
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, page)
                        .stream()
                        .map(mapper::mapBookingToBookingDto)
                        .collect(Collectors.toUnmodifiableList());

            default:
                throw new EntityValidationException("Непредвиденная ошибка! Обратитесь в службу поддержки!");
        }
    }

    @Override
    public BookingInfoDto getLastBooking(Long itemId) {
        Optional<Booking> tmp = bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(itemId,
                LocalDateTime.now(), Status.APPROVED);
        return tmp.map(mapper::mapBookingToBookingInfoDto).orElse(null);
    }

    @Override
    public BookingInfoDto getNextBooking(Long itemId) {
        Optional<Booking> tmp = bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(itemId,
                LocalDateTime.now(), Status.APPROVED);
        return tmp.map(mapper::mapBookingToBookingInfoDto).orElse(null);
    }

    @Override
    public List<BookingDto> getBookingByBooker(Long bookerId, Long itemId) {
        Optional<List<Booking>> tmp = bookingRepository.findByBookerIdAndItemIdAndEndBefore(bookerId,
                itemId, LocalDateTime.now());
        return tmp.map(mapper::mapBookingsToToBookingDtos).orElse(null);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Пользователь не найден!")
        );
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new EntityNotFoundException("Вещь не найдена!")
        );
    }

    private Booking getBooking(Long BookingId) {
        return bookingRepository.findById(BookingId).orElseThrow(
                () -> new EntityNotFoundException("Бронь не найдена!")
        );
    }
}
