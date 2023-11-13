package ru.practicum.shareit.booking.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface BookingDtoBookingMapper {
    @Mapping(target = "itemId", source = "item.id")
    BookingDto mapBookingToBookingDto(Booking booking);

    @Mapping(target = "item", source = "item")
//    @Mapping(target = "item.id", source = "item.id")
//    @Mapping(target = "item.name", source = "item.name")
//    @Mapping(target = "item.description", source = "item.description")
//    @Mapping(target = "item.available", source = "item.available")
//    @Mapping(target = "item.owner", source = "item.owner")
//    @Mapping(target = "item.owner.id", source = "item.owner.id")
//    @Mapping(target = "item.owner.name", source = "item.owner.name")
//    @Mapping(target = "item.owner.email", source = "item.owner.email")
    @Mapping(target = "booker", source = "user")
//    @Mapping(target = "booker.id", source = "user.id")
//    @Mapping(target = "booker.name", source = "user.name")
//    @Mapping(target = "booker.email", source = "user.email")
    @Mapping(target = "id", source = "bookingId")
    Booking mapBookingDtoToBooking(BookingDto dto, Item item, User user, Long bookingId);
}
