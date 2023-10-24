package ru.practicum.shareit.item.model;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

public class ItemMapper {
    public static Item toItem(ItemDto dto, User user, ItemRequest request) {
        return Item.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .owner(user)
                .isAvailable(dto.getAvailable())
                .request(request)
                .build();
    }
}
