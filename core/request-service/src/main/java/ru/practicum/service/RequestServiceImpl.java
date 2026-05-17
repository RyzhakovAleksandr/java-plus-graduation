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

        EventFullDto event = eventClient.getEventInternal(eventId);
        if (event == null) {
            throw new NotFoundException(String.format(Message.EVENT_NOT_FOUND, eventId));
        }

        if (!"PUBLISHED".equals(event.getState())) {
            log.warn(Message.ADD_REQUEST_NOT_PUBLISHED, eventId);
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

        EventFullDto event = eventClient.getEventInternal(eventId);
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

        if (requests.isEmpty()) {
            throw new ForbiddenException(Message.REQUEST_ALREADY_READ);
        }

        for (Request request : requests) {
            if (statusUpdateRequest.getStatus() == StatusRequest.CONFIRMED) {
                if (event.getParticipantLimit() > 0 && confirmedCount >= event.getParticipantLimit()) {
                    throw new ForbiddenException(Message.LIMIT_REQUEST);
                }
                request.setStatus(StatusRequest.CONFIRMED.toString());
                confirmed.add(requestMapper.toParticipationRequestDto(request));
                confirmedCount++;
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

    @Override
    public Long getConfirmedRequestsCount(Long eventId) {
        log.info(Message.LOG_GET_REQUEST_CONFIRMED, eventId);
        return requestRepository.countByEventIdAndStatus(eventId, StatusRequest.CONFIRMED.toString());
    }

    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        log.info(Message.LOG_CANCEL_REQUEST, requestId, userId);
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException(Message.REQUEST_NOT_FOUND));

        if (StatusRequest.CONFIRMED.toString().equals(request.getStatus())) {
            log.warn(Message.CANCEL_CONFIRMED_REQUEST, requestId);
            throw new ForbiddenException(Message.CAN_NOT_CANCEL_REQUEST);
        }

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

    @Override
    public List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId) {
        log.info(Message.LOG_GET_REQUEST_INITIATOR, eventId, userId);

        log.info("=== getEventParticipants ===");
        log.info("userId={}, eventId={}", userId, eventId);

        EventFullDto event = eventClient.getEvent(eventId);

        if (event == null) {
            throw new NotFoundException(String.format(Message.EVENT_NOT_FOUND, eventId));
        }
        log.info("Event initiator id: {}", event.getInitiator().getId());
        log.info("Current userId: {}", userId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ForbiddenException(Message.ONLY_INITIATOR_CAN_VIEW);
        }

        List<Request> requests = requestRepository.findAllByEventId(eventId);
        log.info("Найдено заявок в БД: {}", requests.size());

        return requests.stream()
                .map(requestMapper::toParticipationRequestDto)
                .toList();
    }

    @Override
    public Boolean hasUserVisitedEvent(Long userId, Long eventId) {
        log.info("Checking if user visited event: userId={}, eventId={}", userId, eventId);

        return requestRepository.existsByRequesterIdAndEventIdAndStatus(
                userId, eventId, StatusRequest.CONFIRMED.toString()
        );
    }
}
