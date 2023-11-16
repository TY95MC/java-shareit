package ru.practicum.shareit.item.model;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.InputItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Mapper(componentModel = "spring")
public interface ItemDtoItemMapper {
    ItemDto mapItemToItemDto(Item item);

    InputItemDto mapItemToInputItemDto(Item item);

    Item mapInputItemDtoToItem(InputItemDto dto);
}
