package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.EntityValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InputItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentDtoCommentMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
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
    private final BookingServiceImpl bookingService;
    private final CommentDtoCommentMapperImpl commentMapper;

    @Override
    public InputItemDto addNewItem(Long userId, InputItemDto dto) {
        checkIfUserOrItemExists(userId, null);

        if (dto.getId() == null) {
            Item item = ItemMapper.mapToItem(dto, userRepository.getReferenceById(userId));
            return ItemMapper.mapToInputItemDto(itemRepository.saveAndFlush(item));
        }

        throw new EntityValidationException("Проверьте корректность данных новой вещи!");
    }

    @Override
    public InputItemDto updateItem(Long userId, Long itemId, InputItemDto dto) {
        checkIfUserOrItemExists(userId, itemId);
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

        return ItemMapper.mapToInputItemDto(itemRepository.saveAndFlush(item));
    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        checkIfUserOrItemExists(userId, itemId);
        Item item = itemRepository.getReferenceById(itemId);

        if (item.getOwner().getId() == userId) {
            return mapToItemDtoFull(ItemMapper.mapToItemDto(item));
        } else {
            return mapToItemDtoWithComments(ItemMapper.mapToItemDto(item));
        }
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        checkIfUserOrItemExists(userId, null);
        return itemRepository.findAllByOwnerId(userId).stream()
                .map(ItemMapper::mapToItemDto)
                .map(this::mapToItemDtoFull)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<InputItemDto> findByText(Long userId, String text) {
        checkIfUserOrItemExists(userId, null);

        if (text.isBlank() || text.isEmpty()) {
            return new ArrayList<>();
        }

        return itemRepository.search(text.toLowerCase())
                .stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::mapToInputItemDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        checkIfUserOrItemExists(userId, itemId);

        if (bookingService.getBookingByBooker(userId, itemId) == null ||
                bookingService.getBookingByBooker(userId, itemId).size() == 0) {
            throw new EntityValidationException("Пользователь еще не арендовал данную вещь!");
        }

        commentDto.setCreated(LocalDateTime.now());
        Comment comment = commentMapper.mapCommentDtoToComment(commentDto);
        comment.setAuthor(userRepository.getReferenceById(userId));
        comment.setItem(itemRepository.getReferenceById(itemId));
        comment = commentRepository.saveAndFlush(comment);
        return commentMapper.mapCommentToCommentDto(comment);
    }

    private void checkIfUserOrItemExists(Long userId, Long itemId) {
        if (userId != null && !userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Пользователь не найден!");
        }

        if (itemId != null && !itemRepository.existsById(itemId)) {
            throw new EntityNotFoundException("Вещь не найдена!");
        }
    }

    private ItemDto mapToItemDtoFull(ItemDto dto) {
        BookingInfoDto last = bookingService.getLastBooking(dto.getId());
        BookingInfoDto next = bookingService.getNextBooking(dto.getId());
        dto = mapToItemDtoWithComments(dto);

        if (last != null) {
            dto.setLastBooking(last);
        }

        if (next != null) {
            dto.setNextBooking(next);
        }

        return dto;
    }

    private ItemDto mapToItemDtoWithComments(ItemDto dto) {
        List<CommentDto> list = commentRepository.findAllByItemId(dto.getId())
                .stream()
                .map(commentMapper::mapCommentToCommentDto)
                .collect(Collectors.toUnmodifiableList());
        dto.setComments(list);
        return dto;
    }
}
