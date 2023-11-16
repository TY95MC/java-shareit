package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.EntityValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDtoUserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserDtoUserMapper mapper;

    @Override
    public UserDto addNewUser(UserDto dto) {
        if (dto.getId() != null) {
            throw new EntityValidationException("Новый пользователь не может иметь ID!");
        }

        User user = mapper.mapUserDtoToUser(dto);
        return mapper.mapUserToUserDto(repository.saveAndFlush(user));
    }

    @Override
    public UserDto updateUser(UserDto dto) {
        checkIfExistsById(dto.getId());
        User user = repository.getReferenceById(dto.getId());

        if (dto.getName() != null) {
            user.setName(dto.getName());
        }

        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }

        return mapper.mapUserToUserDto(repository.saveAndFlush(user));
    }

    @Override
    public UserDto getUserById(Long id) {
        checkIfExistsById(id);
        return mapper.mapUserToUserDto(repository.getReferenceById(id));
    }

    @Override
    public List<UserDto> getUsers() {
        return repository.findAll().stream()
                .map(mapper::mapUserToUserDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public void deleteUserById(Long id) {
        checkIfExistsById(id);
        repository.deleteById(id);
        repository.flush();
    }

    private void checkIfExistsById(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Пользователя не существует!");
        }
    }
}
