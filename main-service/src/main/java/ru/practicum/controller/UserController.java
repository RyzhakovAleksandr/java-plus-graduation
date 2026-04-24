package main.java.ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import main.java.ru.practicum.constant.Messages;
import main.java.ru.practicum.dto.GetUsersRequest;
import main.java.ru.practicum.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import ru.practicum.openapi.model.NewUserRequest;
import ru.practicum.openapi.model.UserDto;
import ru.practicum.openapi.api.UserApi;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController implements UserApi {
    private final UserService userService;

    @Override
    public ResponseEntity<Void> deleteUser(Long userId) {
        log.info(Messages.MESSAGE_DELETE_USER, userId);
        userService.deleteUser(userId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<UserDto>> getUsers(
            List<Long> ids,
            Integer from,
            Integer size) {

        log.info(Messages.MESSAGE_GET_USERS, ids, from, size);

        GetUsersRequest request = new GetUsersRequest(ids, from, size);
        List<UserDto> users = userService.getUsers(request);

        return ResponseEntity.ok(users);
    }

    @Override
    public ResponseEntity<UserDto> registerUser(NewUserRequest newUserRequest) {
        log.info(Messages.MESSAGE_REGISTER_USER, newUserRequest);
        UserDto userDto = userService.addUser(newUserRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }
}
