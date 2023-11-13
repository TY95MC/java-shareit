package ru.practicum.shareit.item.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface ItemDtoItemMapper {
    ItemDto mapItemToItemDto(Item item);

    @Mapping(target = "owner", source = "user")
    @Mapping(target = "owner.id", source = "user.id")
    @Mapping(target = "owner.name", source = "user.name")
    @Mapping(target = "owner.email", source = "user.email")
    @Mapping(target = "id", source = "itemDto.id")
    @Mapping(target = "name", source = "itemDto.name")
    Item mapItemDtoToItem(ItemDto itemDto, User user);
}
