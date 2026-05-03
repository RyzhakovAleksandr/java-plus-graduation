package ru.practicum.service;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.constant.Message;
import ru.practicum.dto.GetUsersRequest;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ru.practicum.exception.NotFoundException;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;

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
            throw new ForbiddenException(String.format(Message.EXCEPTION_CONFLICT_EMAIL, newUserRequest.getEmail()));
        }
        return userMapper.toUserDto(userRepository.save(userMapper.toUser(newUserRequest)));
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format(Message.EXCEPTION_NOT_FOUND_USER, userId)));
        return userMapper.toUserDto(user);
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
            throw new NotFoundException(String.format(Message.EXCEPTION_NOT_FOUND_USER, userId));
        }
        userRepository.deleteById(userId);
    }

    private void validateEmail(String email) {
        if (email == null) {
            throw new ValidationException(Message.EXCEPTION_EMAIL_MUST_BE);
        }
        String trimmed = email.trim();
        if (email.isEmpty()) {
            throw new ValidationException(Message.EXCEPTION_EMAIL_EMPTY);
        }
        if (email.length() > 254) {
            throw new ValidationException(Message.EXCEPTION_EMAIL_TOO_LONG);
        }
        String[] parts = trimmed.split("@");
        if (parts.length != 2) {
            throw new ValidationException(Message.EXCEPTION_EMAIL_NOT_CORRECT);
        }
        String localPart = parts[0];
        String domainPart = parts[1];
        if (localPart.length() > 64) {
            throw new ValidationException(Message.EXCEPTION_LOCAL_TOO_LONG);
        }
        if (domainPart.length() > 253) {
            throw new ValidationException(Message.EXCEPTION_DOMAIN_TOO_LONG);
        }
        String[] domainLabels = domainPart.split("\\.");
        for (String label : domainLabels) {
            if (label.length() > 63) {
                throw new ValidationException(Message.EXCEPTION_PATH_DOMAIN_TOO_LONG);
            }
            if (label.startsWith("-") || label.endsWith("-")) {
                throw new ValidationException(Message.EXCEPTION_PATH_DOMAIN_NO_CORRECT);
            }
        }
    }

    private void validateName(String name) {
        if (name == null) {
            throw new ValidationException(Message.EXCEPTION_NAME_MUST_BE);
        }
        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            throw new ValidationException(Message.EXCEPTION_NAME_EMPTY);
        }
        if (trimmed.length() < 2) {
            throw new ValidationException(Message.EXCEPTION_NAME_TOO_SMALL);
        }
        if (trimmed.length() > 250) {
            throw new ValidationException(Message.EXCEPTION_NAME_TOO_LONG);
        }
    }
}
