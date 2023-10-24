package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

interface UserService {
    UserDto addNewUser(UserDto dto);

    UserDto updateUser(UserDto dto);

    UserDto getUserById(Long id);

    List<UserDto> getUsers();

    void deleteUserById(Long id);
}
