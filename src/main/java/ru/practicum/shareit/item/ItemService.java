package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

interface ItemService {
    ItemDto addNewItem(Long userId, ItemDto dto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto dto);

    ItemDto getItem(Long userId, Long itemId);

    List<ItemDto> getItems(Long userId);

    List<ItemDto> findByText(Long userId, String text);
}
