package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.practicum.constant.Message;
import ru.practicum.dto.GetEventsForAdminRequest;
import ru.practicum.dto.GetEventsRequest;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventRequestStatusRequest;
import ru.practicum.dto.EventRequestStatusUpdateResult;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.dto.UpdateEventAdminRequest;
import ru.practicum.dto.UpdateEventUserRequest;
import ru.practicum.service.EventService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping("/users/{userId}/events")
    public ResponseEntity<EventFullDto> addEvent(
            @PathVariable Long userId,
            @Valid @RequestBody NewEventDto newEventDto) {
        log.info(Message.LOG_ADD_EVENT, userId);
        return eventService.addEvent(userId, newEventDto);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> changeRequestStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody EventRequestStatusRequest eventRequestStatusRequest) {
        log.info(Message.LOG_CHANGE_REQUEST_STATUS, userId, eventId);
        return eventService.changeRequestStatus(userId, eventId, eventRequestStatusRequest);
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<EventFullDto> getEvent(@PathVariable Long id) {
        log.info(Message.LOG_GET_EVENT, id);
        return eventService.getEvent(id);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getEventParticipants(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        log.info(Message.LOG_GET_EVENT_PARTICIPANTS, userId, eventId);
        return eventService.getEventParticipants(userId, eventId);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> getEventUser(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        log.info(Message.LOG_GET_EVENT_USER, userId, eventId);
        return eventService.getEventUser(userId, eventId);
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventShortDto>> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info(Message.LOG_GET_EVENTS);
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

    @GetMapping("/admin/events")
    public ResponseEntity<List<EventFullDto>> getEventsAdmin(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info(Message.LOG_GET_ADMIN_EVENTS);
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

    @GetMapping("/users/{userId}/events")
    public ResponseEntity<List<EventShortDto>> getEventsUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info(Message.LOG_GET_EVENTS_USER, userId);
        return eventService.getEventsUser(userId, from, size);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid@RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info(Message.LOG_UPDATE_EVENT, userId, eventId);
        return eventService.updateEvent(userId, eventId, updateEventUserRequest);
    }

    @PatchMapping("/admin/events/{eventId}")
    public ResponseEntity<EventFullDto> updateEventAdmin(
            @PathVariable Long eventId,
            @Valid@RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        log.info(Message.LOG_UPDATE_EVENT_ADMIN, eventId);
        return eventService.updateEventAdmin(eventId, updateEventAdminRequest);
    }
}
