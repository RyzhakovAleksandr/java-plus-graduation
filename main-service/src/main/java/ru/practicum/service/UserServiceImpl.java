package main.java.ru.practicum.service;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import main.java.ru.practicum.constant.Exceptions;
import main.java.ru.practicum.exception.ForbiddenException;
import main.java.ru.practicum.exception.ValidationException;
import main.java.ru.practicum.mapper.UserMapper;
import main.java.ru.practicum.persistence.entity.User;
import main.java.ru.practicum.dto.GetUsersRequest;
import main.java.ru.practicum.persistence.repository.UserRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import main.java.ru.practicum.exception.NotFoundException;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.openapi.model.NewUserRequest;
import ru.practicum.openapi.model.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public UserDto addUser(NewUserRequest newUserRequest) {
        validateEmail(newUserRequest.getEmail());
        validateName(newUserRequest.getName());
        if (userRepository.existsByEmail(newUserRequest.getEmail())) {
            throw new ForbiddenException(String.format(Exceptions.EXCEPTION_CONFLICT_EMAIL, newUserRequest.getEmail()));
        }
        return userMapper.toUserDto(userRepository.save(userMapper.toUser(newUserRequest)));
    }

    @Override
    public List<UserDto> getUsers(GetUsersRequest request) {
        Pageable pageable = PageRequest.of(request.getFrom() / request.getSize(), request.getSize());

        if (request.getIds() != null && !request.getIds().isEmpty()) {
            List<User> users = userRepository.findAllByIdIn(request.getIds(), pageable);
            return users.stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList());
        } else {
            List<User> users = userRepository.findAll(pageable).getContent();
            return users.stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format(Exceptions.EXCEPTION_NOT_FOUND_USER, userId));
        }
        userRepository.deleteById(userId);
    }

    private void validateEmail(String email) {
        if (email == null) {
            throw new ValidationException("Email обязателен для заполнения");
        }
        String trimmed = email.trim();
        if (email.isEmpty()) {
            throw new ValidationException("Email не может быть пустым или состоять только из пробелов");
        }
        if (email.length() > 254) {
            throw new ValidationException("Email не может превышать 254 символа");
        }
        String[] parts = trimmed.split("@");
        if (parts.length != 2) {
            throw new ValidationException("Некорректный формат email");
        }
        String localPart = parts[0];
        String domainPart = parts[1];
        if (localPart.length() > 64) {
            throw new ValidationException("Локальная часть email не может превышать 64 символа");
        }
        if (domainPart.length() > 253) {
            throw new ValidationException("Доменная часть email не может превышать 253 символа");
        }
        String[] domainLabels = domainPart.split("\\.");
        for (String label : domainLabels) {
            if (label.length() > 63) {
                throw new ValidationException("Каждая часть домена не может превышать 63 символа");
            }
            if (label.startsWith("-") || label.endsWith("-")) {
                throw new ValidationException("Части домена не могут начинаться или заканчиваться дефисом");
            }
        }
    }

    private void validateName(String name) {
        if (name == null) {
            throw new ValidationException("Имя обязательно для заполнения");
        }
        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            throw new ValidationException("Имя не может быть пустым или состоять только из пробелов");
        }
        if (trimmed.length() < 2) {
            throw new ValidationException("Имя должно содержать минимум 2 символа");
        }
        if (trimmed.length() > 250) {
            throw new ValidationException("Имя должно содержать не более 250 символов");
        }
    }
}
