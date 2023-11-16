package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.EntityValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.UserDtoUserMapperImpl;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final UserDtoUserMapperImpl userMapper;

    @Override
    public BookingDto create(Long userId, InputBookingDto dto) {
        checkIfUserOrItemOrBookingExists(userId, dto.getItemId(), null);

        if (dto.getStart().equals(dto.getEnd()) || dto.getStart().isAfter(dto.getEnd())) {
            throw new EntityValidationException("Некорректные начало и конец!");
        }

        Item item = itemRepository.getReferenceById(dto.getItemId());

        if (item.getOwner().getId() == userId) {
            throw new EntityNotFoundException("Владелец не может бронировать собственную вещь!");
        }

        checkIfItemNotBelongsToBooker(dto.getItemId(), userId);
        Booking booking = Booking.builder()
                .start(dto.getStart())
                .end(dto.getEnd())
                .item(item)
                .booker(userRepository.getReferenceById(userId))
                .status(dto.getStatus())
                .build();
        booking = bookingRepository.saveAndFlush(booking);
        return mapToBookingDtoFull(BookingMapper.mapToBookingDto(booking), booking);
    }

    @Override
    public BookingDto approve(Long userId, Long bookingId, boolean approved) {
        checkIfUserOrItemOrBookingExists(userId, null, bookingId);
        Booking booking = bookingRepository.getReferenceById(bookingId);

        if (booking.getStatus().equals("APPROVED")) {
            throw new EntityValidationException("Бронь уже подтверждена!");
        }

        checkIfItemNotBelongsToBooker(booking.getItem().getId(), booking.getBooker().getId());

        if (userId != booking.getItem().getOwner().getId()) {
            throw new EntityNotFoundException("Только владелец вещи может подтверждать бронь!");
        } else {
            if (approved) {
                booking.setStatus("APPROVED");
            } else {
                booking.setStatus("REJECTED");
            }
        }
        booking = bookingRepository.saveAndFlush(booking);
        return mapToBookingDtoFull(BookingMapper.mapToBookingDto(booking), booking);
    }

    @Override
    public BookingDto getBookingInfo(Long userId, Long bookingId) {
        checkIfUserOrItemOrBookingExists(userId, null, bookingId);
        Booking booking = bookingRepository.getReferenceById(bookingId);

        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return mapToBookingDtoFull(BookingMapper.mapToBookingDto(booking), booking);
        }
        throw new EntityNotFoundException("Просматривать могут только владелец вещи, либо арендатор!");
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, String state) {
        checkIfUserOrItemOrBookingExists(userId, null, null);

        switch (state) {
            case "ALL":
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId)
                        .stream()
                        .map(booking -> mapToBookingDtoFull(BookingMapper.mapToBookingDto(booking), booking))
                        .collect(Collectors.toUnmodifiableList());

            case "CURRENT":
                return bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartAsc(userId,
                                LocalDateTime.now(), LocalDateTime.now())
                        .stream()
                        .map(booking -> mapToBookingDtoFull(BookingMapper.mapToBookingDto(booking), booking))
                        .collect(Collectors.toUnmodifiableList());

            case "PAST":
                return bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now())
                        .stream()
                        .map(booking -> mapToBookingDtoFull(BookingMapper.mapToBookingDto(booking), booking))
                        .collect(Collectors.toUnmodifiableList());

            case "FUTURE":
                return bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now())
                        .stream()
                        .map(booking -> mapToBookingDtoFull(BookingMapper.mapToBookingDto(booking), booking))
                        .collect(Collectors.toUnmodifiableList());

            case "WAITING":
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, "WAITING")
                        .stream()
                        .map(booking -> mapToBookingDtoFull(BookingMapper.mapToBookingDto(booking), booking))
                        .collect(Collectors.toUnmodifiableList());

            case "REJECTED":
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, "REJECTED")
                        .stream()
                        .map(booking -> mapToBookingDtoFull(BookingMapper.mapToBookingDto(booking), booking))
                        .collect(Collectors.toUnmodifiableList());
            default:
                throw new EntityValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<BookingDto> getBookingsByOwner(Long userId, String state) {
        checkIfUserOrItemOrBookingExists(userId, null, null);
        switch (state) {
            case "ALL":
                return bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId)
                        .stream()
                        .map(booking -> mapToBookingDtoFull(BookingMapper.mapToBookingDto(booking), booking))
                        .collect(Collectors.toUnmodifiableList());

            case "CURRENT":
                return bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                                LocalDateTime.now(), LocalDateTime.now())
                        .stream()
                        .map(booking -> mapToBookingDtoFull(BookingMapper.mapToBookingDto(booking), booking))
                        .collect(Collectors.toUnmodifiableList());

            case "PAST":
                return bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now())
                        .stream()
                        .map(booking -> mapToBookingDtoFull(BookingMapper.mapToBookingDto(booking), booking))
                        .collect(Collectors.toUnmodifiableList());

            case "FUTURE":
                return bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now())
                        .stream()
                        .map(booking -> mapToBookingDtoFull(BookingMapper.mapToBookingDto(booking), booking))
                        .collect(Collectors.toUnmodifiableList());

            case "WAITING":
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, "WAITING")
                        .stream()
                        .map(booking -> mapToBookingDtoFull(BookingMapper.mapToBookingDto(booking), booking))
                        .collect(Collectors.toUnmodifiableList());

            case "REJECTED":
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, "REJECTED")
                        .stream()
                        .map(booking -> mapToBookingDtoFull(BookingMapper.mapToBookingDto(booking), booking))
                        .collect(Collectors.toUnmodifiableList());

            default:
                throw new EntityValidationException("Unknown state: " + state);
        }
    }

    @Override
    public BookingInfoDto getLastBooking(Long itemId) {
        Optional<Booking> tmp = bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(itemId,
                LocalDateTime.now(), "APPROVED");
        return tmp.map(BookingMapper::mapToBookingInfoDto).orElse(null);
    }

    @Override
    public BookingInfoDto getNextBooking(Long itemId) {
        Optional<Booking> tmp = bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(itemId,
                LocalDateTime.now(), "APPROVED");
        return tmp.map(BookingMapper::mapToBookingInfoDto).orElse(null);
    }

    @Override
    public List<BookingDto> getBookingByBooker(Long bookerId, Long itemId) {
        Optional<List<Booking>> tmp = bookingRepository.findByBookerIdAndItemIdAndEndBefore(bookerId,
                itemId, LocalDateTime.now());
        return tmp.map(BookingMapper::mapToBookingDtos).orElse(null);
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

    private void checkIfItemNotBelongsToBooker(Long itemId, Long bookerId) {
        checkIfItemIsAvailable(itemId);
        Item item = itemRepository.getReferenceById(itemId);

        if (item.getOwner().getId() == bookerId) {
            throw new EntityValidationException("Пользователь не может бронировать собственную вещь!");
        }
    }

    private void checkIfItemIsAvailable(Long itemId) {
        Item item = itemRepository.getReferenceById(itemId);

        if (!item.getAvailable()) {
            throw new EntityValidationException("Вещь недоступна к бронированию!");
        }
    }

    private BookingDto mapToBookingDtoFull(BookingDto dto, Booking booking) {
        dto.setItem(ItemMapper.mapToInputItemDto(booking.getItem()));
        dto.setBooker(userMapper.mapUserToUserDto(booking.getBooker()));
        return dto;
    }
}
