package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.practicum.constant.Message;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;

import java.util.List;

@Slf4j
@Component
public class EventClientFallbackFactory implements FallbackFactory<EventClient> {
    @Override
    public EventClient create(Throwable cause) {
        return new EventClient() {
            @Override
            public EventFullDto getEvent(Long id) {
                log.warn(Message.GET_EVENT_SERVICE_NOT_AVAILABLE, id);
                return null;
            }
            @Override
            public List<EventShortDto> getEventsByIds(List<Long> ids) {
                log.warn(Message.GET_EVENTS_SERVICE_NOT_AVAILABLE);
                return List.of();
            }

            @Override
            public EventShortDto getEventShort(Long id) {
                log.warn(Message.GET_SHORT_EVENT_SERVICE_NOT_AVAILABLE, id);
                return null;
            }

            @Override
            public boolean hasEventsByCategory(Long categoryId) {
                log.warn("event-service недоступен, предполагаем что событий нет");
                return false;
            }

            @Override
            public EventFullDto getEventInternal(Long id) {
                log.warn("event-service недоступен для internal запроса события {}", id);
                return null;
            }
        };
    }
}
