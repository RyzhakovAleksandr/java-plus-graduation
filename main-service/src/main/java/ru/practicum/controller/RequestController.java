package main.java.ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import main.java.ru.practicum.constant.Messages;
import main.java.ru.practicum.service.RequestService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import ru.practicum.openapi.api.RequestApi;
import ru.practicum.openapi.model.ParticipationRequestDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RequestController implements RequestApi {
    private final RequestService requestService;

    @Override
    public ResponseEntity<ParticipationRequestDto> addParticipationRequest(Long userId, Long eventId) {
        ParticipationRequestDto participationRequestDto = requestService.addRequest(userId, eventId);
        log.info(Messages.MESSAGE_ADDED_REQUEST, participationRequestDto.getId(), participationRequestDto.getStatus());

        return ResponseEntity.status(HttpStatus.CREATED).body(participationRequestDto);
    }

    @Override
    public ResponseEntity<ParticipationRequestDto> cancelRequest(Long userId, Long requestId) {
        //requestService.cancelRequest(userId, requestId); <- два вызова в одном методе -_-
        log.info(Messages.MESSAGE_CANCEL_REQUEST, requestId, userId);

        return ResponseEntity.ok(requestService.cancelRequest(userId, requestId));
    }

    @Override
    public ResponseEntity<List<ParticipationRequestDto>> getUserRequests(Long userId) {
        List<ParticipationRequestDto> requests = requestService.getRequestsByUser(userId);
        log.info(Messages.MESSAGE_GET_REQUESTS, requests.size(), userId);

        return ResponseEntity.ok(requests);
    }
}
