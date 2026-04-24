package main.java.ru.practicum.service;

import main.java.ru.practicum.dto.GetUsersRequest;

import ru.practicum.openapi.model.NewUserRequest;
import ru.practicum.openapi.model.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(NewUserRequest newUserRequest);

    List<UserDto> getUsers(GetUsersRequest request);

    void deleteUser(Long userId);
}
