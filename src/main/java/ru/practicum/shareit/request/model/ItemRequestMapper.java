package ru.practicum.shareit.request.model;

public class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequest itemRequest) {
        return ItemRequest.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(itemRequest.getRequestor())
                .created(itemRequest.getCreated())
                .build();
    }
}
