package ru.practicum.shareit.booking.model;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;

@Mapper(componentModel = "spring")
public interface BookingDtoBookingMapper {
    InputBookingDto mapBookingToInputBookingDto(Booking booking);

    BookingDto mapBookingToBookingDto(Booking booking);

    BookingInfoDto mapBookingToBookingInfo(Booking booking);
}
