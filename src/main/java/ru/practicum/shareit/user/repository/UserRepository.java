package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User addUser(UserDto user);

    User updateUser(UserDto user);

    User getUserById(Long id);

    List<User> getUsers();

    void deleteUserById(Long id);
}
