package main.java.ru.practicum.controller;

import lombok.RequiredArgsConstructor;

import main.java.ru.practicum.dto.GetEventsRequest;
import main.java.ru.practicum.service.EventService;
import main.java.ru.practicum.dto.GetEventsForAdminRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import ru.practicum.openapi.api.EventApi;
import ru.practicum.openapi.model.EventFullDto;
import ru.practicum.openapi.model.EventRequestStatusRequest;
import ru.practicum.openapi.model.EventRequestStatusUpdateResult;
import ru.practicum.openapi.model.EventShortDto;
import ru.practicum.openapi.model.NewEventDto;
import ru.practicum.openapi.model.ParticipationRequestDto;
import ru.practicum.openapi.model.UpdateEventAdminRequest;
import ru.practicum.openapi.model.UpdateEventUserRequest;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventController implements EventApi {
    private final EventService eventService;

    @Override
    public ResponseEntity<EventFullDto> addEvent(Long userId, NewEventDto newEventDto) {
        return eventService.addEvent(userId, newEventDto);
    }

    @Override
    public ResponseEntity<EventRequestStatusUpdateResult> changeRequestStatus(Long userId, Long eventId,
                                                                               EventRequestStatusRequest eventRequestStatusRequest) {
        return eventService.changeRequestStatus(userId, eventId, eventRequestStatusRequest);
    }

    @Override
    public ResponseEntity<EventFullDto> getEvent(Long id) {
        return eventService.getEvent(id);
    }

    @Override
    public ResponseEntity<List<ParticipationRequestDto>> getEventParticipants(Long userId, Long eventId) {
        return eventService.getEventParticipants(userId, eventId);
    }

    @Override
    public ResponseEntity<EventFullDto> getEventUser(Long userId, Long eventId) {
        return eventService.getEventUser(userId, eventId);
    }

    @Override
    public ResponseEntity<List<EventShortDto>> getEvents(String text, List<Long> categories, Boolean paid,
                                                          String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                                          String sort, Integer from, Integer size) {
        return eventService.getEvents(GetEventsRequest.builder()
                            .text(text)
                            .categories(categories)
                            .paid(paid)
                            .rangeStart(rangeStart)
                            .rangeEnd(rangeEnd)
                            .onlyAvailable(onlyAvailable)
                            .sort(sort)
                            .from(from)
                            .size(size)
                            .build());
    }

    @Override
    public ResponseEntity<List<EventFullDto>> getEventsAdmin(List<Long> users, List<String> states, List<Long> categories,
                                                              String rangeStart, String rangeEnd, Integer from,
                                                              Integer size) {
        return eventService.getEventsAdmin(GetEventsForAdminRequest.builder()
                        .users(users)
                        .states(states)
                        .categories(categories)
                        .rangeStart(rangeStart)
                        .rangeEnd(rangeEnd)
                        .from(from)
                        .size(size)
                        .build());
    }

    @Override
    public ResponseEntity<List<EventShortDto>> getEventsUser(Long userId, Integer from, Integer size) {
        return eventService.getEventsUser(userId, from, size);
    }

    @Override
    public ResponseEntity<EventFullDto> updateEvent(Long userId, Long eventId,
                                                     UpdateEventUserRequest updateEventUserRequest) {
        return eventService.updateEvent(userId, eventId, updateEventUserRequest);
    }

    @Override
    public ResponseEntity<EventFullDto> updateEventAdmin(Long eventId,
                                                          UpdateEventAdminRequest updateEventAdminRequest) {
        return eventService.updateEventAdmin(eventId, updateEventAdminRequest);
    }
}
