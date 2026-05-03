package ru.practicum.service;

import ru.practicum.dto.GetUsersRequest;

import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(NewUserRequest newUserRequest);

    List<UserDto> getUsers(GetUsersRequest request);

    void deleteUser(Long userId);

    UserDto getUserById(Long userId);
}
