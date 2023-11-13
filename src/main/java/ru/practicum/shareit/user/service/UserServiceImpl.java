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

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserDtoUserMapper mapper;

    @Override
    public User addNewUser(UserDto dto) {
        if (dto.getId() != null) {
            throw new EntityValidationException("Новый пользователь не может иметь ID!");
        }
        User user = mapper.mapUserDtoToUser(dto);
        return repository.saveAndFlush(user);
    }

    @Override
    public User updateUser(UserDto dto) {
        checkIfExistsById(dto.getId());
        User user = repository.getReferenceById(dto.getId());
        if (dto.getName() != null) {
            user.setName(dto.getName());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        return repository.saveAndFlush(user);
    }

    @Override
    public User getUserById(Long id) {
        checkIfExistsById(id);
        return repository.getReferenceById(id);
    }

    @Override
    public List<User> getUsers() {
        return Collections.unmodifiableList(repository.findAll());
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
