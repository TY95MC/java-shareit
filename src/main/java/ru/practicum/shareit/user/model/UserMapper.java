package ru.practicum.shareit.user.model;

import ru.practicum.shareit.user.dto.UserDto;

public class UserMapper {
    public static User toUser(UserDto user) {
        return User.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
