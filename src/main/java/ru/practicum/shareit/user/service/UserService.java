package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
public interface UserService {
    User addNewUser(UserDto dto);

    User updateUser(UserDto dto);

    User getUserById(Long id);

    List<User> getUsers();

    void deleteUserById(Long id);
}
