package ru.practicum.shareit.booking.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface BookingDtoBookingMapper {
    BookingDto mapBookingToBookingDto(Booking booking);
    @Mapping(target = "item", source = "item")
    @Mapping(target = "item.id", source = "item.id")
    @Mapping(target = "item.name", source = "item.name")
    @Mapping(target = "booker", source = "user")
    @Mapping(target = "booker.id", source = "user.id")
    @Mapping(target = "booker.name", source = "user.name")
    @Mapping(target = "id", source = "bookingId")
    Booking mapBookingDtoToBooking(BookingDto dto, Item item, User user, Long bookingId);
}
