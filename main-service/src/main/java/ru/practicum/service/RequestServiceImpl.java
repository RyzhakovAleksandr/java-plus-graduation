package main.java.ru.practicum.service;

import lombok.RequiredArgsConstructor;

import main.java.ru.practicum.constant.Exceptions;
import main.java.ru.practicum.exception.ForbiddenException;
import main.java.ru.practicum.exception.NotFoundException;
import main.java.ru.practicum.exception.ValidationException;
import main.java.ru.practicum.mapper.RequestMapper;
import main.java.ru.practicum.persistence.entity.Event;
import main.java.ru.practicum.persistence.entity.Request;
import main.java.ru.practicum.persistence.entity.User;
import main.java.ru.practicum.persistence.repository.EventRepository;
import main.java.ru.practicum.persistence.repository.RequestRepository;
import main.java.ru.practicum.persistence.repository.UserRepository;
import main.java.ru.practicum.persistence.status.StatusRequest;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.openapi.model.EventFullDto;
import ru.practicum.openapi.model.EventRequestStatusRequest;
import ru.practicum.openapi.model.EventRequestStatusUpdateResult;
import ru.practicum.openapi.model.ParticipationRequestDto;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static main.java.ru.practicum.persistence.status.StatusRequest.*;

@Service
@Transactional
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final RequestMapper requestMapper;

    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format(Exceptions.EXCEPTION_EVENT_NOT_FOUND, eventId)));
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(Exceptions.EXCEPTION_NOT_FOUND_USER, userId)));

        if (!requestRepository.getRequestsByUserIdAndEventId(userId, eventId).isEmpty()) {
            throw new ForbiddenException(Exceptions.EXCEPTION_REQUEST_EXIST);
        }

        if (userId.equals(event.getInitiator())) {
            throw new ForbiddenException(Exceptions.EXCEPTION_REQUEST_INITIATOR_OWN);
        }

        if (!event.getState().equals(EventFullDto.StateEnum.PUBLISHED.toString())) {
            throw new ForbiddenException(Exceptions.EXCEPTION_REQUEST_NOT_PUBLISHED);
        }

        if (event.getParticipantLimit() != 0 && event.getParticipantLimit()
                <= requestRepository.countByEventIdAndStatus(eventId, CONFIRMED.toString())) {
            throw new ForbiddenException(Exceptions.EXCEPTION_REQUEST_LIMIT);
        }

        Request request = new Request();
        request.setCreated(OffsetDateTime.now());
        request.setEvent(eventId);
        request.setRequester(userId);

        if (event.getRequestModeration() && event.getParticipantLimit() != 0) {
            request.setStatus(String.valueOf(PENDING));
        } else {
            request.setStatus(String.valueOf(CONFIRMED));
        }

        return requestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId,
                                                              EventRequestStatusRequest statusUpdateRequest) {
        User initiator = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format(Exceptions.EXCEPTION_NOT_FOUND_USER, userId)));
        Event event = eventRepository.getEventByUserIdAndEventId(userId, eventId).orElseThrow(() ->
                new NotFoundException(String.format(Exceptions.EXCEPTION_EVENT_NOT_FOUND, eventId)));

        if (!event.getInitiator().equals(initiator.getId())) {
            throw new ValidationException(Exceptions.EXCEPTION_USER_NOT_INITIATOR);
        }

        long confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, CONFIRMED.toString());

        if (event.getParticipantLimit() > 0 && event.getParticipantLimit() <= confirmedRequests) {
            throw new ForbiddenException(Exceptions.EXCEPTION_REQUEST_LIMIT);
        }

        List<ParticipationRequestDto> confirmed = new ArrayList<>();
        List<ParticipationRequestDto> rejected = new ArrayList<>();
        List<Request> requests = requestRepository.findAllByEventIdAndIdInAndStatus(eventId,
                statusUpdateRequest.getRequestIds().toArray(Long[]::new), PENDING.toString());

        for (int i = 0; i < requests.size(); i++) {
            Request request = requests.get(i);
            if (statusUpdateRequest.getStatus().equals(EventRequestStatusRequest.StatusEnum.REJECTED)) {
                request.setStatus(String.valueOf(StatusRequest.REJECTED));
                rejected.add(requestMapper.toParticipationRequestDto(request));
            }

            if (statusUpdateRequest.getStatus().equals(EventRequestStatusRequest.StatusEnum.CONFIRMED)
                    && event.getParticipantLimit() > 0
                    && (confirmedRequests + i) < event.getParticipantLimit()) {
                request.setStatus(String.valueOf(StatusRequest.CONFIRMED));
                confirmed.add(requestMapper.toParticipationRequestDto(request));
            } else {
                request.setStatus(String.valueOf(StatusRequest.REJECTED));
                rejected.add(requestMapper.toParticipationRequestDto(request));
            }
        }
        return new EventRequestStatusUpdateResult(confirmed, rejected);
    }

    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException(Exceptions.EXCEPTION_NOT_FOUND));
        request.setStatus(String.valueOf(StatusRequest.CANCELED));

        return requestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    public List<ParticipationRequestDto> getRequestsByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format(Exceptions.EXCEPTION_NOT_FOUND_USER, userId));
        }

        return requestRepository.findAllByRequesterId(userId).stream()
                .map(requestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }
}
