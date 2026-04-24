package main.java.ru.practicum.service;

import ru.practicum.openapi.model.EventRequestStatusRequest;
import ru.practicum.openapi.model.EventRequestStatusUpdateResult;
import ru.practicum.openapi.model.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto addRequest(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId,
                                                       EventRequestStatusRequest statusUpdateRequest);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getRequestsByUser(Long userId);


}
