package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.InputItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Service
public interface ItemService {
    InputItemDto addNewItem(Long userId, InputItemDto dto);

    InputItemDto updateItem(Long userId, Long itemId, InputItemDto dto);

    ItemDto getItem(Long userId, Long itemId);

    List<ItemDto> getItems(Long userId);

    List<InputItemDto> findByText(Long userId, String text);

    CommentDto addComment(Long userId, Long itemId, CommentDto comment);
}
