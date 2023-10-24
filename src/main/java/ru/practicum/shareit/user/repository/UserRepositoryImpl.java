package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.EntityValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> userIdToUser = new HashMap<>();
    private Long id = 1L;

    @Override
    public User addUser(@Valid UserDto dto) {
        if (dto.getEmail() == null || dto.getName() == null) {
            throw new EntityValidationException("Некорректно заполнены данные!");
        }
        checkIfEmailAlreadyExists(dto.getEmail());
        dto.setId(generateId());
        if (!userIdToUser.containsKey(dto.getId())) {
            userIdToUser.put(dto.getId(), UserMapper.toUser(dto));
        } else {
            throw new ValidationException("Пользователь уже существует!");
        }
        return getUserById(dto.getId());
    }

    @Override
    public User updateUser(UserDto dto) {
        checkIfUserExists(dto.getId());
        checkIfEmailAlreadyExists(dto.getEmail(), dto.getId());
        final User user = getUserById(dto.getId());
        if (dto.getName() == null) {
            dto.setName(user.getName());
        }
        if (dto.getEmail() == null) {
            dto.setEmail(user.getEmail());
        }
        userIdToUser.put(dto.getId(), UserMapper.toUser(dto));
        return getUserById(dto.getId());
    }

    @Override
    public User getUserById(Long id) {
        checkIfUserExists(id);
        return userIdToUser.get(id);
    }

    @Override
    public List<User> getUsers() {
        return List.copyOf(userIdToUser.values());
    }

    @Override
    public void deleteUserById(Long id) {
        checkIfUserExists(id);
        userIdToUser.remove(id);
    }

    private void checkIfUserExists(Long id) {
        if (!userIdToUser.containsKey(id)) {
            throw new EntityNotFoundException("Пользователь не найден!");
        }
    }

    private void checkIfEmailAlreadyExists(String email) {
        if (userIdToUser.values().stream().anyMatch(user -> user.getEmail().equals(email))) {
            throw new ValidationException("Пользователь с таким email уже существует!");
        }
    }

    private void checkIfEmailAlreadyExists(String email, Long userId) {
        if (userIdToUser.values().stream().anyMatch(user -> user.getEmail().equals(email) && !user.getId().equals(userId))) {
            throw new ValidationException("Пользователь с таким email уже существует!");
        }
    }

    private Long generateId() {
        return id++;
    }
}
