package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public UserDto addNewUser(UserDto dto) {
        return UserMapper.toUserDto(repository.addUser(dto));
    }

    @Override
    public UserDto updateUser(UserDto dto) {
        return UserMapper.toUserDto(repository.updateUser(dto));
    }

    @Override
    public UserDto getUserById(Long id) {
        return UserMapper.toUserDto(repository.getUserById(id));
    }

    @Override
    public List<UserDto> getUsers() {
        return repository.getUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public void deleteUserById(Long id) {
        repository.deleteUserById(id);
    }
}
