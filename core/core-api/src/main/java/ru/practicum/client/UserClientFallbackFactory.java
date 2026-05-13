package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.practicum.constant.Message;
import ru.practicum.dto.UserDto;

@Slf4j
@Component
public class UserClientFallbackFactory implements FallbackFactory<UserClient> {

    @Override
    public UserClient create(Throwable cause) {
        return new UserClient() {

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
