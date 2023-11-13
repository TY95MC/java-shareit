package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

@Service
public interface BookingService {
    Booking create(Long userId, BookingDto dto);

    Booking approve(Long userId, Long bookingId, boolean approved);

    Booking getBookingInfo(Long userId, Long bookingId);

    List<Booking> getUserBookings(Long userId, State state);

    List<Booking> getBookingsByOwner(Long userId, State state);
}
