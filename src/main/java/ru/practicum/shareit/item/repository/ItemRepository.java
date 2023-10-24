package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item addNewItem(Long userId, ItemDto dto);

    Item updateItem(Long userId, Long itemId, ItemDto dto);

    Item getItem(Long userId, Long itemId);

    List<Item> getItems(Long userId);

    List<Item> findByText(Long userId, String text);
}
