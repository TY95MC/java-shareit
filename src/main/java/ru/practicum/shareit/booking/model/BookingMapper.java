package ru.practicum.shareit.booking.model;

public class BookingMapper {
    public static Booking toBooking(Booking booking) {
        return Booking.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }
}
