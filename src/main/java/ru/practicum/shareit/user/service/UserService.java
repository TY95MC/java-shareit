package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
public interface UserService {
    UserDto addNewUser(UserDto dto);

    UserDto updateUser(UserDto dto);

    UserDto getUserById(Long id);

    List<UserDto> getUsers();

    void deleteUserById(Long id);
}
