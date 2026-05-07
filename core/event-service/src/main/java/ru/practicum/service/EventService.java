package ru.practicum.service;

import ru.practicum.dto.GetEventsForAdminRequest;
import ru.practicum.dto.GetEventsRequest;

import org.springframework.http.ResponseEntity;

import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventRequestStatusRequest;
import ru.practicum.dto.EventRequestStatusUpdateResult;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.dto.UpdateEventAdminRequest;
import ru.practicum.dto.UpdateEventUserRequest;

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
