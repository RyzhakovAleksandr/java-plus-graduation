package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.practicum.constant.Message;
import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;

import java.util.List;

@Slf4j
@Component
public class UserClientFallbackFactory implements FallbackFactory<UserClient> {

    @Override
    public UserClient create(Throwable cause) {
        return new UserClient() {

            @Override
            public UserDto registerUser(NewUserRequest newUserRequest) {
                log.error(Message.REGISTER_USER_SERVICE_NOT_AVAILABLE, cause.getMessage());
                throw new RuntimeException(Message.USER_SERVICE_NOT_AVAILABLE);
            }

            @Override
            public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
                log.warn(Message.GET_USERS_USER_SERVICE_NOT_AVAILABLE, cause.getMessage());
                return List.of();
            }

            @Override
            public void deleteUser(Long userId) {
                log.error(Message.DELETE_USER_SERVICE_NOT_AVAILABLE, userId);
                throw new RuntimeException(Message.USER_SERVICE_NOT_AVAILABLE);
            }

            @Override
            public UserDto getUser(Long userId) {
                log.warn(Message.GET_USER_SERVICE_NOT_AVAILABLE, userId);

                UserDto defaultUser = new UserDto();
                defaultUser.setId(userId);
                defaultUser.setName("Unknown User");
                defaultUser.setEmail("unknown@example.com");
                return defaultUser;
            }
        };
    }
}
