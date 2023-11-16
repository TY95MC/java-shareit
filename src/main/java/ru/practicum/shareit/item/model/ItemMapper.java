package ru.practicum.shareit.item.model;

import ru.practicum.shareit.item.dto.InputItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto mapToItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(new ArrayList<>())
                .build();
    }

    public static List<ItemDto> mapToItemDtos(List<Item> items) {
        return items.stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toUnmodifiableList());
    }

    public static InputItemDto mapToInputItemDto(Item item) {
        return InputItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static Item mapToItem(InputItemDto dto, User user) {
        return Item.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .owner(user)
                .build();
    }
}
