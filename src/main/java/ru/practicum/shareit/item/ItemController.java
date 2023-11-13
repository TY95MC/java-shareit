package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

import static ru.practicum.shareit.constants.Constants.USER_ID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto add(@RequestHeader(USER_ID) @NotNull @Positive Long userId,
                       @RequestBody @Valid ItemDto dto) {
        return itemService.addNewItem(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(USER_ID) @NotNull @Positive Long userId,
                              @PathVariable @NotNull @Positive Long itemId,
                              @RequestBody ItemDto dto) {
        return itemService.updateItem(userId, itemId, dto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader(USER_ID) @NotNull @Positive Long userId,
                           @PathVariable @NotNull @Positive Long itemId) {
        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> get(@RequestHeader(USER_ID) @NotNull @Positive Long userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(USER_ID) @NotNull @Positive Long userId,
                                @RequestParam String text) {
        return itemService.findByText(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(USER_ID) @NotNull @Positive Long userId, CommentDto comment, Long itemId) {
        return itemService.addComment(userId, itemId, comment);
    }

//    @GetMapping("/{itemId}")
//    public List<CommentDto> getCommentsOfItem(@PathVariable @NotNull @Positive Long itemId) {
//        return itemService.getItemComments(itemId);
//    }
//
//    @GetMapping
//    public List<CommentDto> getCommentsOfUserItems(@RequestHeader(USER_ID) @NotNull @Positive Long userId) {
//        return itemService.getUserItemsComments(userId);
//    }
}