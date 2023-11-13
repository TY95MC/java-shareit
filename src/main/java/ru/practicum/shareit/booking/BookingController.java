package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;

import java.util.List;

import static ru.practicum.shareit.constants.Constants.USER_ID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public Booking create(@RequestHeader(USER_ID) Long userId,
                             @RequestBody @Valid BookingDto dto) {
        dto.setStatus(Status.WAITING);
        return bookingService.create(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public Booking approve(@RequestHeader(USER_ID) Long userId,
                           @PathVariable Long bookingId,
                           @RequestParam boolean approved) {
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking getBookingInfo(@RequestHeader(USER_ID) Long userId, @PathVariable Long bookingId) {
        return bookingService.getBookingInfo(userId, bookingId);
    }

    @GetMapping
    public List<Booking> getUserBookings(@RequestHeader(USER_ID) Long userId,
                                         @RequestParam(defaultValue = "ALL") State state) {
        return bookingService.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<Booking> getBookingsByOwner(@RequestHeader(USER_ID) Long userId,
                                   @RequestParam(defaultValue = "ALL") State state) {
        return bookingService.getBookingsByOwner(userId, state);
    }
}
