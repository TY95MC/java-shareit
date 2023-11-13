package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.EntityValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.CommentDtoCommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDtoItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ItemDtoItemMapper mapper;
    private final CommentDtoCommentMapper commentMapper;

    @Override
    public ItemDto addNewItem(Long userId, ItemDto dto) {
        checkIfUserOrItemExists(userId, null);
        if (dto.getId() == null) {
            // user = ;
            Item item = mapper.mapItemDtoToItem(dto, userRepository.getReferenceById(userId));
            return mapper.mapItemToItemDto(itemRepository.saveAndFlush(item));
        }
        throw new EntityValidationException("Проверьте корректность данных новой вещи!");
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto dto) {
        checkIfItemBelongsToUser(itemId, userId);
        Item item = itemRepository.getReferenceById(itemId);
        if (dto.getDescription() != null) {
            item.setDescription(dto.getDescription());
        }
        if (dto.getName() != null) {
            item.setName(dto.getName());
        }
        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
        }
        return mapper.mapItemToItemDto(itemRepository.saveAndFlush(item));
    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        checkIfItemBelongsToUser(itemId, userId);
        return mapper.mapItemToItemDto(itemRepository.getReferenceById(itemId));
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        checkIfUserOrItemExists(userId, null);
        return itemRepository.findAllByOwnerId(userId).stream()
                .map(mapper::mapItemToItemDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<ItemDto> findByText(Long userId, String text) {
        checkIfUserOrItemExists(userId, null);
        if (text.isBlank() || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(text, text)
                .stream()
                .filter(Item::getAvailable)
                .map(mapper::mapItemToItemDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        checkIfUserOrItemExists(userId, itemId);
        commentDto.setCreated(LocalDateTime.now());
        Comment comment = commentMapper.commentDtoToComment(
                commentDto,
                userRepository.getReferenceById(userId),
                itemRepository.getReferenceById(itemId)
        );
        return commentMapper.commentToCommentDto(commentRepository.saveAndFlush(comment));
    }

    @Override
    public List<CommentDto> getItemComments(Long itemId) {
        checkIfUserOrItemExists(null, itemId);
        return commentRepository.findAllByItemId(itemId).stream()
                .map(commentMapper::commentToCommentDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<CommentDto> getUserItemsComments(Long userId) {
        checkIfUserOrItemExists(userId, null);
        return commentRepository.findAllByAuthorId(userId).stream()
                .map(commentMapper::commentToCommentDto)
                .collect(Collectors.toUnmodifiableList());
    }

    private void checkIfUserOrItemExists(Long userId, Long itemId) {
        if (userId != null && !userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Пользователь не найден!");
        }
        if (itemId != null && !itemRepository.existsById(itemId)) {
            throw new EntityNotFoundException("Вещь не найдена!");
        }
    }

    private void checkIfItemBelongsToUser(Long itemId, Long userId) {
        checkIfUserOrItemExists(userId, itemId);
        Item item = itemRepository.getReferenceById(itemId);
        if (item.getOwner().getId() != userId) {
            throw new EntityValidationException("Данный пользователь не является владельцем вещи!");
        }
    }
}
