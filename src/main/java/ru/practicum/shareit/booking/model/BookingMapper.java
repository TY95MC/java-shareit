package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;

import java.util.List;
import java.util.stream.Collectors;


public class BookingMapper {
    public static BookingDto mapToBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .build();
    }

    public static List<BookingDto> mapToBookingDtos(List<Booking> booking) {
        return booking.stream()
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toUnmodifiableList());
    }

    public static Booking mapToBooking(BookingInfoDto dto) {
        return Booking.builder()
                .id(dto.getId())
                .build();
    }

    public static BookingInfoDto mapToBookingInfoDto(Booking booking) {
        return BookingInfoDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
