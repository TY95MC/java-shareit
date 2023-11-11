package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentDto;

import java.util.List;

@Service
public interface ItemService {
    ItemDto addNewItem(Long userId, ItemDto dto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto dto);

    ItemDto getItem(Long userId, Long itemId);

    List<ItemDto> getItems(Long userId);

    List<ItemDto> findByText(Long userId, String text);

    CommentDto addComment(Long userId, Long itemId, CommentDto comment);

    List<CommentDto> getItemComments(Long itemId);

    List<CommentDto> getUserItemsComments(Long userId);
}
