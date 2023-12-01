package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InputItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.constants.Constants.FROM;
import static ru.practicum.shareit.constants.Constants.SIZE;
import static ru.practicum.shareit.constants.Constants.USER_ID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public InputItemDto add(@RequestHeader(USER_ID) @NotNull @Positive Long userId,
                            @RequestBody @Valid InputItemDto dto) {
        return itemService.addNewItem(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public InputItemDto updateItem(@RequestHeader(USER_ID) @NotNull @Positive Long userId,
                                   @PathVariable @NotNull @Positive Long itemId,
                                   @RequestBody InputItemDto dto) {
        return itemService.updateItem(userId, itemId, dto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader(USER_ID) @NotNull @Positive Long userId,
                           @PathVariable @NotNull @Positive Long itemId) {
        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> get(@RequestHeader(USER_ID) @NotNull @Positive Long userId,
                             @RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
                             @RequestParam(defaultValue = SIZE) @Positive Integer size) {
        return itemService.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<InputItemDto> search(@RequestHeader(USER_ID) @NotNull @Positive Long userId,
                                     @RequestParam String text,
                                     @RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
                                     @RequestParam(defaultValue = SIZE) @Positive Integer size) {
        return itemService.findByText(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(USER_ID) @NotNull @Positive Long userId,
                                 @PathVariable @NotNull @Positive Long itemId,
                                 @RequestBody @Valid CommentDto comment) {
        return itemService.addComment(userId, itemId, comment);
    }
}