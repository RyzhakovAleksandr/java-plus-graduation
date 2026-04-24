package main.java.ru.practicum.mapper;

import main.java.ru.practicum.persistence.entity.User;

import org.mapstruct.Mapper;

import ru.practicum.openapi.model.NewUserRequest;
import ru.practicum.openapi.model.UserDto;
import ru.practicum.openapi.model.UserShortDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toUserDto(User user);

    User toUser(NewUserRequest newUserRequest);

    UserShortDto userToUserShortDto(User user);
}
