package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingInfoDto;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    Long id;
    String name;
    String description;
    Boolean available;
    BookingInfoDto lastBooking;
    BookingInfoDto nextBooking;
    List<CommentDto> comments;
}
