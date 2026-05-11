package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.practicum.constant.Message;
import ru.practicum.dto.EventRequestStatusRequest;
import ru.practicum.dto.EventRequestStatusUpdateResult;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.service.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RequestController {
    private final RequestService requestService;

    private static final String USERS_REQUESTS = "/users/{userId}/requests";
    private static final String USERS_REQUESTS_CANCEL = "/users/{userId}/requests/{requestId}/cancel";
    private static final String USERS_EVENTS_REQUESTS = "/users/{userId}/events/{eventId}/requests";
    private static final String EVENTS_CONFIRMED_COUNT = "/events/{eventId}/confirmed-count";

    @PostMapping(USERS_REQUESTS)
    public ResponseEntity<ParticipationRequestDto> addParticipationRequest(
            @PathVariable Long userId,
            @RequestParam Long eventId) {
        log.info(Message.ADD_REQUEST, userId, eventId);

        ParticipationRequestDto participationRequestDto = requestService.addRequest(userId, eventId);
        return ResponseEntity.status(HttpStatus.CREATED).body(participationRequestDto);
    }

    @PatchMapping(USERS_REQUESTS_CANCEL)
    public ResponseEntity<ParticipationRequestDto> cancelRequest(
            @PathVariable Long userId,
            @PathVariable Long requestId) {
        log.info(Message.CANCEL_REQUEST, userId, requestId);

        ParticipationRequestDto result = requestService.cancelRequest(userId, requestId);
        return ResponseEntity.ok(result);
    }

    @GetMapping(USERS_REQUESTS)
    public ResponseEntity<List<ParticipationRequestDto>> getUserRequests(@PathVariable Long userId) {
        log.info(Message.GET_USER_REQUEST, userId);

        List<ParticipationRequestDto> requests = requestService.getRequestsByUser(userId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping(USERS_EVENTS_REQUESTS)
    public ResponseEntity<List<ParticipationRequestDto>> getEventParticipants(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        log.info(Message.GET_REQUEST, userId, eventId);
        List<ParticipationRequestDto> result = requestService.getEventParticipants(userId, eventId);
        return ResponseEntity.ok(result);
    }

    @PatchMapping(USERS_EVENTS_REQUESTS)
    public ResponseEntity<EventRequestStatusUpdateResult> updateRequestStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody EventRequestStatusRequest request) {
        log.info(Message.UPDATE_REQUEST, userId, eventId);
        EventRequestStatusUpdateResult result = requestService.updateRequestStatus(userId, eventId, request);
        return ResponseEntity.ok(result);
    }

    @GetMapping(EVENTS_CONFIRMED_COUNT)
    public ResponseEntity<Long> getConfirmedRequestsCount(@PathVariable Long eventId) {
        log.info(Message.GET_CONFIRMED_REQUEST, eventId);
        Long count = requestService.getConfirmedRequestsCount(eventId);
        return ResponseEntity.ok(count);
    }
}
