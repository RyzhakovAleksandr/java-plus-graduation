package ru.practicum.service;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.client.EventClient;
import ru.practicum.client.UserClient;
import ru.practicum.constant.Message;
import ru.practicum.dto.UserDto;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Request;
import ru.practicum.repository.RequestRepository;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventRequestStatusRequest;
import ru.practicum.dto.EventRequestStatusUpdateResult;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.status.StatusRequest;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final UserClient userClient;
    private final EventClient eventClient;


    @Override
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        log.info(Message.LOG_ADD_REQUEST, userId, eventId);

        UserDto user = userClient.getUser(userId);
        if (user == null) {
            throw new NotFoundException(String.format(Message.USER_NOT_FOUND, userId));
        }

        EventFullDto event = eventClient.getEvent(eventId);
        if (event == null) {
            throw new NotFoundException(String.format(Message.EVENT_NOT_FOUND, eventId));
        }

        if (!"PUBLISHED".equals(event.getState())) {
            throw new ForbiddenException(Message.EVENT_NOT_PUBLISHED);
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ForbiddenException(Message.INITIATOR_CAN_NOT_BE);
        }

        List<Request> existingRequests = requestRepository.getRequestsByUserIdAndEventId(userId, eventId);
        if (!existingRequests.isEmpty()) {
            throw new ForbiddenException(Message.EXISTING_REQUEST);
        }

        Long confirmedCount = requestRepository.countByEventIdAndStatus(eventId, StatusRequest.CONFIRMED.toString());
        if (event.getParticipantLimit() > 0 && confirmedCount >= event.getParticipantLimit()) {
            throw new ForbiddenException(Message.LIMIT_REQUEST);
        }

        String status;
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            status = StatusRequest.CONFIRMED.toString();
        } else {
            status = StatusRequest.PENDING.toString();
        }

        Request request = Request.builder()
                .created(OffsetDateTime.now())
                .event(eventId)
                .requester(userId)
                .status(status)
                .build();

        request = requestRepository.save(request);
        log.info(Message.LOG_CREATED_REQUEST, request.getId(), status);

        return requestMapper.toParticipationRequestDto(request);
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId,
                                                              EventRequestStatusRequest statusUpdateRequest) {
        log.info(Message.LOG_UPDATE_REQUEST, eventId, userId);

        EventFullDto event = eventClient.getEvent(eventId);
        if (event == null) {
            throw new NotFoundException(String.format(Message.EVENT_NOT_FOUND, eventId));
        }

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ValidationException(Message.ONLY_INITIATOR_CAN_CHANGE);
        }

        List<ParticipationRequestDto> confirmed = new ArrayList<>();
        List<ParticipationRequestDto> rejected = new ArrayList<>();

        List<Request> requests = requestRepository.findAllByEventIdAndIdInAndStatus(eventId,
                statusUpdateRequest.getRequestIds().toArray(Long[]::new), StatusRequest.PENDING.toString());

        long confirmedCount = requestRepository.countByEventIdAndStatus(eventId, StatusRequest.CONFIRMED.toString());

        for (Request request : requests) {
            if (statusUpdateRequest.getStatus() == StatusRequest.CONFIRMED) {
                if (event.getParticipantLimit() == 0 || confirmedCount < event.getParticipantLimit()) {
                    request.setStatus(StatusRequest.CONFIRMED.toString());
                    confirmed.add(requestMapper.toParticipationRequestDto(request));
                    confirmedCount++;
                } else {
                    request.setStatus(StatusRequest.REJECTED.toString());
                    rejected.add(requestMapper.toParticipationRequestDto(request));
                }
            } else if (statusUpdateRequest.getStatus() == StatusRequest.REJECTED) {
                request.setStatus(StatusRequest.REJECTED.toString());
                rejected.add(requestMapper.toParticipationRequestDto(request));
            }
        }

        requestRepository.saveAll(requests);
        log.info(Message.LOG_CONFIRMED_REQUEST, confirmed.size(), rejected.size());

        if (statusUpdateRequest.getStatus() == StatusRequest.CONFIRMED) {
            return EventRequestStatusUpdateResult.builder().confirmedRequests(confirmed).build();
        } else {
            return EventRequestStatusUpdateResult.builder().rejectedRequests(rejected).build();
        }
    }

    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        log.info(Message.LOG_CANCEL_REQUEST, requestId, userId);
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException(Message.REQUEST_NOT_FOUND));

        request.setStatus(StatusRequest.CANCELED.toString());
        request = requestRepository.save(request);

        return requestMapper.toParticipationRequestDto(request);
    }

    public List<ParticipationRequestDto> getRequestsByUser(Long userId) {
        log.info(Message.LOG_REQUESTS_BY_USER,  userId);
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(requestMapper::toParticipationRequestDto)
                .toList();
    }
}
