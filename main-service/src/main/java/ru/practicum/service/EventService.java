package main.java.ru.practicum.service;

import main.java.ru.practicum.dto.GetEventsForAdminRequest;
import main.java.ru.practicum.dto.GetEventsRequest;

import org.springframework.http.ResponseEntity;

import ru.practicum.openapi.model.EventFullDto;
import ru.practicum.openapi.model.EventRequestStatusRequest;
import ru.practicum.openapi.model.EventRequestStatusUpdateResult;
import ru.practicum.openapi.model.EventShortDto;
import ru.practicum.openapi.model.NewEventDto;
import ru.practicum.openapi.model.ParticipationRequestDto;
import ru.practicum.openapi.model.UpdateEventAdminRequest;
import ru.practicum.openapi.model.UpdateEventUserRequest;

import java.util.List;

public interface EventService {
    ResponseEntity<EventFullDto> addEvent(Long userId, NewEventDto newEventDto);

    ResponseEntity<EventRequestStatusUpdateResult> changeRequestStatus(Long userId,
                                                                       Long eventId,
                                                                       EventRequestStatusRequest eventRequestStatusRequest);

    ResponseEntity<EventFullDto> getEvent(Long id);

    ResponseEntity<List<ParticipationRequestDto>> getEventParticipants(Long userId, Long eventId);

    ResponseEntity<EventFullDto> getEventUser(Long userId, Long eventId);

    ResponseEntity<List<EventShortDto>> getEvents(GetEventsRequest request);

    ResponseEntity<List<EventFullDto>> getEventsAdmin(GetEventsForAdminRequest request);

    ResponseEntity<List<EventShortDto>> getEventsUser(Long userId, Integer from, Integer size);

    ResponseEntity<EventFullDto> updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    ResponseEntity<EventFullDto> updateEventAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);
}
