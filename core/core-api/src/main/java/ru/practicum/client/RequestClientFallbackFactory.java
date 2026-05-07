package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.practicum.constant.Message;
import ru.practicum.dto.EventRequestStatusRequest;
import ru.practicum.dto.EventRequestStatusUpdateResult;
import ru.practicum.dto.ParticipationRequestDto;

import java.util.List;

@Slf4j
@Component
public class RequestClientFallbackFactory implements FallbackFactory<RequestClient> {
    @Override
    public RequestClient create(Throwable cause) {
        return new RequestClient() {

            @Override
            public List<ParticipationRequestDto> getUserRequests(Long userId) {
                log.warn(Message.GET_REQUEST_SERVICE_NOT_AVAILABLE, userId);
                return List.of();
            }

            @Override
            public ParticipationRequestDto addRequest(Long userId, Long eventId) {
                log.error(Message.ADD_REQUEST_SERVICE_NOT_AVAILABLE, eventId);
                throw new RuntimeException(Message.REQUEST_SERVICE_NOT_AVAILABLE);
            }

            @Override
            public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
                log.error(Message.CANCEL_REQUEST_SERVICE_NOT_AVAILABLE, requestId);
                throw new RuntimeException(Message.REQUEST_SERVICE_NOT_AVAILABLE);
            }

            @Override
            public List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId) {
                log.warn(Message.GET_REQUEST_EVENTS_SERVICE_NOT_AVAILABLE, eventId);
                return List.of();
            }

            @Override
            public EventRequestStatusUpdateResult changeRequestStatus(Long userId, Long eventId, EventRequestStatusRequest request) {
                log.error(Message.CHANGE_REQUEST_SERVICE_NOT_AVAILABLE, eventId);
                throw new RuntimeException(Message.REQUEST_SERVICE_NOT_AVAILABLE);
            }
        };
    }
}
