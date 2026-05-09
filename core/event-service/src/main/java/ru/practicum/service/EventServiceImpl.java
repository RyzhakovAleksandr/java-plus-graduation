package ru.practicum.service;

import dto.EndpointHitDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import ru.practicum.client.CategoryClient;
import ru.practicum.client.RequestClient;
import ru.practicum.client.UserClient;
import ru.practicum.constant.Message;
import ru.practicum.constant.Values;
import ru.practicum.dto.GetEventsForAdminRequest;
import ru.practicum.dto.GetEventsRequest;
import ru.practicum.dto.UserShortDto;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.MismatchDateException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.NotMeetRulesEditionException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.LocationMapper;
import ru.practicum.model.Event;
import ru.practicum.model.LocationEntity;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.LocationRepository;
import ru.practicum.specification.EventSpecification;
import ru.practicum.client.StatsClient;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.UserDto;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventRequestStatusRequest;
import ru.practicum.dto.EventRequestStatusUpdateResult;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.dto.UpdateEventAdminRequest;
import ru.practicum.dto.UpdateEventUserRequest;
import ru.practicum.status.StateAction;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.constant.Values.APPLICATION;
import static ru.practicum.constant.Values.EVENTS_GET_URI;
import static ru.practicum.constant.Values.EVENT_GET_URI;
import static ru.practicum.constant.Values.EWM_IP;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final UserClient userClient;
    private final CategoryClient categoryClient;
    private final RequestClient requestClient;
    private final StatsClient statsClient;

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;

    private final EventMapper eventMapper;
    private final LocationMapper locationMapper;
    private final EventSpecification eventSpecification;

    @Override
    public ResponseEntity<EventFullDto> addEvent(Long userId, NewEventDto newEventDto) {
        log.info(Message.MESSAGE_ADD_EVENT, userId, newEventDto);

        UserDto user = userClient.getUser(userId);
        if (user == null) {
            throw new NotFoundException(String.format(Message.EXCEPTION_USER_NOT_FOUND, userId));
        }

        CategoryDto category = categoryClient.getCategory(newEventDto.getCategory());
        if (category == null) {
            throw new NotFoundException(String.format(Message.EXCEPTION_CATEGORY_NOT_FOUND, newEventDto.getCategory()));
        }

        validateEventFields(newEventDto);
        checkDate(newEventDto.getEventDate());

        LocationEntity location = locationRepository
                .save(locationMapper.locationToLocationEntity(newEventDto.getLocation()));

        Event event = eventMapper.newEventDtoToEvent(newEventDto);
        event.setInitiator(userId);
        event.setLocation(location.getId());
        event.setState("PENDING");
        event.setConfirmedRequests(0);
        event.setViews(0L);
        event = eventRepository.save(event);

        return ResponseEntity.status(HttpStatus.CREATED).body(getEventFullDto(event, location));
    }

    @Override
    public ResponseEntity<EventRequestStatusUpdateResult> changeRequestStatus(
            Long userId, Long eventId, EventRequestStatusRequest eventRequestStatusRequest) {
        log.info(Message.MESSAGE_CHANGE_STATUS);

        EventRequestStatusUpdateResult result = requestClient.changeRequestStatus(userId, eventId, eventRequestStatusRequest);
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<EventFullDto> getEvent(Long id) {
        log.info(Message.MESSAGE_GET_EVENT_BY_ID, id);

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Message.EXCEPTION_NOT_FOUND));

        if (event.getViews() == null || event.getViews() == 0) {
            event.setViews(1L);
            eventRepository.save(event);
        }

        if (!"PUBLISHED".equals(event.getState())) {
            throw new NotFoundException(Message.EXCEPTION_NOT_PUBLISHED);
        }

        LocationEntity location = locationRepository.findById(event.getLocation())
                .orElseThrow(() -> new NotFoundException(Message.EXCEPTION_NOT_FOUND));

        try {
            EndpointHitDto hit = EndpointHitDto.builder()
                    .uri(String.format(EVENT_GET_URI, id))
                    .app(APPLICATION)
                    .ip(EWM_IP)
                    .timestamp(LocalDateTime.now())
                    .build();
            statsClient.saveHit(hit);
        } catch (Exception e) {
            log.warn(Message.CAN_NOT_SEND_STATUS, e.getMessage());
        }

        return ResponseEntity.ok(getEventFullDto(event, location));
    }

    @Override
    public ResponseEntity<List<ParticipationRequestDto>> getEventParticipants(Long userId, Long eventId) {
        log.info(Message.MESSAGE_GET_PARTICIPANTS, userId, eventId);

        List<ParticipationRequestDto> result = requestClient.getEventParticipants(userId, eventId);
        return ResponseEntity.ok(List.of());
    }

    @Override
    public ResponseEntity<EventFullDto> getEventUser(Long userId, Long eventId) {
        log.info(Message.MESSAGE_GET_EVENTS_BY_USER_ID_AND_EVENT_ID, eventId, userId);

        Event event = eventRepository.getEventByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> new NotFoundException(Message.EXCEPTION_NOT_FOUND));
        LocationEntity location = locationRepository.findById(event.getLocation())
                .orElseThrow(() -> new NotFoundException(Message.EXCEPTION_NOT_FOUND));

        return ResponseEntity.status(HttpStatus.OK).body(getEventFullDto(event, location));
    }

    @Override
    public ResponseEntity<List<EventShortDto>> getEvents(GetEventsRequest request) {
        log.info(Message.MESSAGE_GET_EVENTS);

        validateDateRange(request.getRangeStart(), request.getRangeEnd());

        List<EventShortDto> list = eventSpecification.getPagesFromGetEventsRequest(request, eventRepository)
                .stream()
                .map(this::getEventShortDto)
                .toList();

        try {
            EndpointHitDto hit = EndpointHitDto.builder()
                    .uri(EVENTS_GET_URI)
                    .app(APPLICATION)
                    .ip(EWM_IP)
                    .timestamp(LocalDateTime.now())
                    .build();
            statsClient.saveHit(hit);
        } catch (Exception e) {
            log.warn(Message.CAN_NOT_SEND_STATUS, e.getMessage());
        }

        return ResponseEntity.ok(list);
    }

    @Override
    public ResponseEntity<List<EventFullDto>> getEventsAdmin(GetEventsForAdminRequest request) {
        log.info(Message.MESSAGE_GET_EVENTS_FOR_ADMIN);

        Page<Event> events = eventSpecification.getPagesFromGetEventsForAdminRequest(request, eventRepository);
        Map<Long, UserDto> users = events.stream()
                .map(Event::getInitiator)
                .distinct()
                .map(userClient::getUser)
                .collect(Collectors.toMap(UserDto::getId, u -> u));
        Map<Long, CategoryDto> categories = events.stream().
                map(Event::getCategory)
                .distinct()
                .map(categoryClient::getCategory)
                .collect(Collectors.toMap(CategoryDto::getId, c -> c));
        Map<Long, LocationEntity> locations = locationRepository.findAllById(
                        events.stream()
                                .map(Event::getLocation)
                                .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(LocationEntity::getId, l -> l));

        List<EventFullDto> list = events.stream()
                .map(e -> {
                    EventFullDto dto = eventMapper.eventToEventFullDto(e);
                    dto.setInitiator(toUserShortDto(users.get(e.getInitiator())));
                    dto.setCategory(categories.get(e.getCategory()));
                    dto.setLocation(locationMapper.locationEntityToLocation(locations.get(e.getLocation())));
                    return dto;
                }).toList();

        return ResponseEntity.ok(list);
    }

    @Override
    public ResponseEntity<List<EventShortDto>> getEventsUser(Long userId, Integer from, Integer size) {
        log.info(Message.MESSAGE_GET_EVENTS_FOR_USER);

        List<EventShortDto> events = eventRepository.getEventsUser(userId, from, size).stream()
                .map(this::getEventShortDto)
                .toList();

        return ResponseEntity.ok(events);
    }

    @Override
    public ResponseEntity<EventFullDto> updateEvent(Long userId, Long eventId,
                                                    UpdateEventUserRequest updateEventUserRequest) {
        log.info(Message.MESSAGE_UPDATE_EVENT);

        checkDate(updateEventUserRequest.getEventDate());

        Event event = eventRepository.getEventByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> new NotFoundException(Message.EXCEPTION_NOT_FOUND));

        if ("PUBLISHED".equals(event.getState())) {
            throw new ForbiddenException(Message.EXCEPTION_CANT_UPDATE_PUBLISHED);
        }

        LocationEntity location;

        if (updateEventUserRequest.getLocation() != null) {
            location = locationRepository
                    .save(locationMapper.locationToLocationEntity(updateEventUserRequest.getLocation()));
        } else {
            location = locationRepository.findById(event.getLocation())
                    .orElseThrow(() -> new NotFoundException(Message.EXCEPTION_LOCAL_NOT_FOUND));
        }

        eventMapper.updateEventUserRequestToEvent(event, location.getId(), updateEventUserRequest);

        if (updateEventUserRequest.getStateAction() != null) {
            switch (updateEventUserRequest.getStateAction()) {
                case CANCEL_REVIEW:
                    if ("PENDING".equals(event.getState()) || "CANCELED".equals(event.getState())) {
                        event.setState("CANCELED");
                    } else {
                        throw new NotMeetRulesEditionException(Message.EXCEPTION_NOT_MEET_RULES);
                    }
                    break;
                case SEND_TO_REVIEW:
                    if ("CANCELED".equals(event.getState())) {
                        event.setState("PENDING");
                    } else {
                        throw new NotMeetRulesEditionException(Message.EXCEPTION_NOT_MEET_RULES);
                    }
                    break;
                default:
                    throw new NotMeetRulesEditionException(Message.EXCEPTION_NOT_MEET_RULES);
            }
        }

        eventRepository.save(event);

        return ResponseEntity.ok(getEventFullDto(event, location));
    }

    @Override
    public ResponseEntity<EventFullDto> updateEventAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        log.info(Message.MESSAGE_UPDATE_EVENT);

        checkDate(updateEventAdminRequest.getEventDate());

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(Message.EXCEPTION_NOT_FOUND));

        LocationEntity location;
        if (updateEventAdminRequest.getLocation() != null) {
            location = locationRepository
                    .save(locationMapper.locationToLocationEntity(updateEventAdminRequest.getLocation()));
            event.setLocation(location.getId());
        } else {
            location = locationRepository.findById(event.getLocation())
                    .orElseThrow(() -> new NotFoundException(Message.EXCEPTION_LOCAL_NOT_FOUND));
            updateEventAdminRequest.setLocation(locationMapper.locationEntityToLocation(location));
        }

        eventMapper.updateEventAdminRequestToEvent(event, location.getId(), updateEventAdminRequest);

        if (updateEventAdminRequest.getStateAction() != null) {
            if (!"PUBLISHED".equals(event.getState())
                    && updateEventAdminRequest.getStateAction().equals(StateAction.REJECT_EVENT)) {
                event.setState("CANCELED");
            } else if ("PENDING".equals(event.getState())
                    && updateEventAdminRequest.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                event.setState("PUBLISHED");
            } else {
                throw new NotMeetRulesEditionException(Message.EXCEPTION_NOT_MEET_RULES);
            }
        }

        return ResponseEntity.ok(getEventFullDto(eventRepository.save(event), location));
    }

    private EventFullDto getEventFullDto(Event event, LocationEntity location) {
        UserDto user = userClient.getUser(event.getInitiator());
        CategoryDto category = categoryClient.getCategory(event.getCategory());

        EventFullDto eventFullDto = eventMapper.eventToEventFullDto(event);
        eventFullDto.setPublishedOn(OffsetDateTime.now().format(DateTimeFormatter.ofPattern(Values.DATE_TIME_PATTERN)));
        eventFullDto.setInitiator(toUserShortDto(user));
        eventFullDto.setCategory(category);
        eventFullDto.setLocation(locationMapper.locationEntityToLocation(location));

        return eventFullDto;
    }

    private EventShortDto getEventShortDto(Event event) {
        UserDto user = userClient.getUser(event.getInitiator());
        CategoryDto category = categoryClient.getCategory(event.getCategory());

        EventShortDto eventShortDto = eventMapper.eventToEventShortDto(event);
        eventShortDto.setInitiator(toUserShortDto(user));
        eventShortDto.setCategory(category);

        return eventShortDto;
    }

    private UserShortDto toUserShortDto(UserDto user) {
        if (user == null) return null;
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    private void checkDate(String time) {
        if (time == null) {
            return;
        }

        ZoneId zoneId = ZoneId.of("UTC");
        OffsetDateTime dateTime = ZonedDateTime.of(
                LocalDateTime.parse(time, DateTimeFormatter.ofPattern(Values.DATE_TIME_PATTERN)),
                zoneId).toOffsetDateTime();

        if (Duration.between(OffsetDateTime.now(zoneId), dateTime).toHours() < 2) {
            throw new MismatchDateException(Message.EXCEPTION_DATE_MISMATCH);
        }
    }

    private void validateEventFields(NewEventDto newEventDto) {
        if (newEventDto.getAnnotation() != null && newEventDto.getAnnotation().trim().isEmpty()) {
            throw new ValidationException(Message.EXCEPTION_FIELD_ANNOTATION_NOT_HAS_SPACE);
        }

        if (newEventDto.getDescription() != null && newEventDto.getDescription().trim().isEmpty()) {
            throw new ValidationException(Message.EXCEPTION_FIELD_DESCRIPTION_NOT_HAS_SPACE);
        }

        if (newEventDto.getTitle() != null && newEventDto.getTitle().trim().isEmpty()) {
            throw new ValidationException(Message.EXCEPTION_FIELD_TITLE_NOT_HAS_SPACE);
        }
    }

    private void validateDateRange(String rangeStart, String rangeEnd) {
        if (rangeStart != null && rangeEnd != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Values.DATE_TIME_PATTERN);

            LocalDateTime start = LocalDateTime.parse(rangeStart, formatter);
            LocalDateTime end = LocalDateTime.parse(rangeEnd, formatter);

            if (start.isAfter(end)) {
                throw new ValidationException(Message.EXCEPTION_WRONG_DATE_RANGE);
            }
        }
    }
}
