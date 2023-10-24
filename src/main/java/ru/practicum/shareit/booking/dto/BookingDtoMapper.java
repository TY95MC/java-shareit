package ru.practicum.shareit.booking.dto;

public class BookingDtoMapper {
    public static BookingDto toBookingDto(BookingDto dto) {
        return BookingDto.builder()
                .id(dto.getId())
                .start(dto.getStart())
                .end(dto.getEnd())
                .item(dto.getItem())
                .booker(dto.getBooker())
                .status(dto.getStatus())
                .build();
    }
}
