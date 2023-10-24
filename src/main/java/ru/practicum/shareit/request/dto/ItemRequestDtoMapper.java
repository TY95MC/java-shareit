package ru.practicum.shareit.request.dto;

public class ItemRequestDtoMapper {
    public static ItemRequestDto toItemRequest(ItemRequestDto dto) {
        return ItemRequestDto.builder()
                .id(dto.getId())
                .description(dto.getDescription())
                .requestor(dto.getRequestor())
                .created(dto.getCreated())
                .build();
    }
}
