package main.java.ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import main.java.ru.practicum.constant.Exceptions;
import main.java.ru.practicum.constant.Messages;
import main.java.ru.practicum.constant.Values;
import main.java.ru.practicum.dto.GetEventsForAdminRequest;
import main.java.ru.practicum.dto.GetEventsRequest;
import main.java.ru.practicum.exception.*;
import main.java.ru.practicum.mapper.CategoryMapper;
import main.java.ru.practicum.mapper.EventMapper;
import main.java.ru.practicum.mapper.LocationMapper;
import main.java.ru.practicum.mapper.UserMapper;
import main.java.ru.practicum.persistence.entity.Category;
import main.java.ru.practicum.persistence.entity.Event;
import main.java.ru.practicum.persistence.entity.LocationEntity;
import main.java.ru.practicum.persistence.entity.Request;
import main.java.ru.practicum.persistence.entity.User;
import main.java.ru.practicum.persistence.repository.CategoryRepository;
import main.java.ru.practicum.persistence.repository.EventRepository;
import main.java.ru.practicum.persistence.repository.LocationRepository;
import main.java.ru.practicum.persistence.repository.RequestRepository;
import main.java.ru.practicum.persistence.repository.UserRepository;
import main.java.ru.practicum.persistence.status.StatusRequest;
import main.java.ru.practicum.specification.EventSpecification;
import main.java.ru.practicum.util.EwmClient;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.openapi.model.EventFullDto;
import ru.practicum.openapi.model.EventRequestStatusRequest;
import ru.practicum.openapi.model.EventRequestStatusUpdateResult;
import ru.practicum.openapi.model.EventShortDto;
import ru.practicum.openapi.model.NewEventDto;
import ru.practicum.openapi.model.ParticipationRequestDto;
import ru.practicum.openapi.model.UpdateEventAdminRequest;
import ru.practicum.openapi.model.UpdateEventUserRequest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final CategoryRepository categoryRepository;

    private final LocationRepository locationRepository;

    private final RequestRepository requestRepository;

    private final EventMapper eventMapper;

    private final UserMapper userMapper;

    private final CategoryMapper categoryMapper;

    private final LocationMapper locationMapper;

    private final EventSpecification eventSpecification;

    @Override
    public ResponseEntity<EventFullDto> addEvent(Long userId, NewEventDto newEventDto) {
        log.info(Messages.MESSAGE_ADD_EVENT, userId, newEventDto);

        validateEventFields(newEventDto);
        checkDate(newEventDto.getEventDate());

        LocationEntity location = locationRepository
                .save(locationMapper.locationToLocationEntity(newEventDto.getLocation()));

        Event event = eventMapper.newEventDtoToEvent(newEventDto);
        event.setInitiator(userId);
        event.setLocation(location.getId());
        event.setState(EventFullDto.StateEnum.PENDING.toString());
        event.setConfirmedRequests(0);
        event.setViews(0L);
        event = eventRepository.save(event);

        return ResponseEntity.status(HttpStatus.CREATED).body(getEventFullDto(event, location));
    }

    @Override
    public ResponseEntity<EventRequestStatusUpdateResult> changeRequestStatus(Long userId,
                                                                              Long eventId,
                                                                              EventRequestStatusRequest eventRequestStatusRequest) {
        log.info(Messages.MESSAGE_CHANGE_STATUS);

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(Exceptions.EXCEPTION_NOT_FOUND_USER, userId)));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(Exceptions.EXCEPTION_NOT_FOUND));
        List<Request> requests = requestRepository
                .getRequestsByIds(eventRequestStatusRequest.getRequestIds().toArray(Long[]::new));

        if (eventRequestStatusRequest.getStatus().equals(EventRequestStatusRequest.StatusEnum.CONFIRMED)) {
            long confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, StatusRequest.CONFIRMED.toString());
            long newConfirmedCount = confirmedRequests + requests.size();

            if (event.getParticipantLimit() > 0 && newConfirmedCount > event.getParticipantLimit()) {
                throw new LimitRequestsExceededException(Exceptions.EXCEPTION_LIMIT_EXCEEDED);
            }
        }

        List<ParticipationRequestDto> participationRequestDto =
                requestRepository.saveAll(requests.stream().peek(r -> {
            if (!r.getStatus().equals(StatusRequest.PENDING.toString())) {
                throw new NotRespondStatusException(Messages.MESSAGE_NOT_RESPOND_STATUS);
            } else {
                r.setStatus(eventRequestStatusRequest.getStatus().toString());
            }
        }).toList()).stream().map(eventMapper::requestToParticipationRequestDto).toList();

        EventRequestStatusUpdateResult result;

        if (eventRequestStatusRequest.getStatus().equals(EventRequestStatusRequest.StatusEnum.CONFIRMED)) {
            event.setConfirmedRequests(event.getConfirmedRequests() + participationRequestDto.size());
            result = EventRequestStatusUpdateResult.builder().confirmedRequests(participationRequestDto).build();
        } else {
            result = EventRequestStatusUpdateResult.builder().rejectedRequests(participationRequestDto).build();
        }

        eventRepository.save(event);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<EventFullDto> getEvent(Long id) {
        log.info(Messages.MESSAGE_GET_EVENT_BY_ID, id);

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Exceptions.EXCEPTION_NOT_FOUND));

        if (event.getViews() == 0) {
            event.setViews(1L);
            eventRepository.save(event);
        }

        if (!event.getState().equals(EventFullDto.StateEnum.PUBLISHED.toString())) {
            throw new NotFoundException(Exceptions.EXCEPTION_NOT_PUBLISHED);
        }

        LocationEntity location = locationRepository.findById(event.getLocation())
                .orElseThrow(() -> new NotFoundException(Exceptions.EXCEPTION_NOT_FOUND));

        EwmClient.sendEvent(Values.EVENT_GET_URI, id);

        return ResponseEntity.ok(getEventFullDto(event, location));
    }

    @Override
    public ResponseEntity<List<ParticipationRequestDto>> getEventParticipants(Long userId, Long eventId) {
        log.info(Messages.MESSAGE_GET_PARTICIPANTS, userId, eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(Exceptions.EXCEPTION_EVENT_NOT_FOUND));

        if (!event.getInitiator().equals(userId)) {
            throw new ForbiddenException(Exceptions.EXCEPTION_ONLY_INITIATOR);
        }

        List<Request> requests = requestRepository.findAllByEventId(eventId);

        List<ParticipationRequestDto> result = requests.stream()
                .map(eventMapper::requestToParticipationRequestDto)
                .toList();

        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<EventFullDto> getEventUser(Long userId, Long eventId) {
        log.info(Messages.MESSAGE_GET_EVENTS_BY_USER_ID_AND_EVENT_ID, eventId, userId);

        Event event = eventRepository.getEventByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> new NotFoundException(Exceptions.EXCEPTION_NOT_FOUND));
        LocationEntity location = locationRepository.findById(event.getLocation())
                .orElseThrow(() -> new NotFoundException(Exceptions.EXCEPTION_NOT_FOUND));

        return ResponseEntity.status(HttpStatus.OK).body(getEventFullDto(event, location));
    }

    @Override
    public ResponseEntity<List<EventShortDto>> getEvents(GetEventsRequest request) {
        log.info(Messages.MESSAGE_GET_EVENTS);

        validateDateRange(request.getRangeStart(), request.getRangeEnd());

        List<EventShortDto> list = eventSpecification.getPagesFromGetEventsRequest(request, eventRepository)
                .stream()
                .map(this::getEventShortDto)
                .toList();

        EwmClient.sendEvents(Values.EVENTS_GET_URI);

        return ResponseEntity.ok(list);
    }

    @Override
    public ResponseEntity<List<EventFullDto>> getEventsAdmin(GetEventsForAdminRequest request) {
        log.info(Messages.MESSAGE_GET_EVENTS_FOR_ADMIN);

        Page<Event> events = eventSpecification.getPagesFromGetEventsForAdminRequest(request, eventRepository);
        Map<Long, User> users = userRepository.findAllById(events.map(Event::getInitiator).toList())
                .stream().collect(Collectors.toMap(User::getId, u -> u));
        Map<Long, Category> category = categoryRepository.findAllById(events.map(Event::getCategory).toList())
                .stream().collect(Collectors.toMap(Category::getId, c -> c));
        Map<Long, LocationEntity> location = locationRepository.findAllById(events.map(Event::getLocation).toList())
                .stream().collect(Collectors.toMap(LocationEntity::getId, l -> l));

        List<EventFullDto> list = events.stream()
                .map(e -> eventMapper.eventToEventFullDto(e)
                        .initiator(userMapper.userToUserShortDto(users.get(e.getInitiator())))
                        .category(categoryMapper.toCategoryDto(category.get(e.getCategory())))
                        .location(locationMapper.locationEntityToLocation(location.get(e.getLocation()))))
                .toList();

        return ResponseEntity.ok(list);
    }

    @Override
    public ResponseEntity<List<EventShortDto>> getEventsUser(Long userId, Integer from, Integer size) {
        log.info(Messages.MESSAGE_GET_EVENTS_FOR_USER);

        List<EventShortDto> events = eventRepository.getEventsUser(userId, from, size).stream()
                .map(this::getEventShortDto)
                .toList();

        return ResponseEntity.ok(events);
    }

    @Override
    public ResponseEntity<EventFullDto> updateEvent(Long userId, Long eventId,
                                                    UpdateEventUserRequest updateEventUserRequest) {
        log.info(Messages.MESSAGE_UPDATE_EVENT);

        checkDate(updateEventUserRequest.getEventDate());

        Event event = eventRepository.getEventByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> new NotFoundException(Exceptions.EXCEPTION_NOT_FOUND));

        if (event.getState().equals(EventFullDto.StateEnum.PUBLISHED.toString())) {
            throw new ForbiddenException(Exceptions.EXCEPTION_CANT_UPDATE_PUBLISHED);
        }

        LocationEntity location = null;

        if (updateEventUserRequest.getLocation() != null) {
            location = locationRepository
                    .save(locationMapper.locationToLocationEntity(updateEventUserRequest.getLocation()));
        } else {
            location = locationRepository.findById(event.getLocation()).get();
        }

        eventMapper.updateEventUserRequestToEvent(event, location.getId(), updateEventUserRequest);

        if (updateEventUserRequest.getStateAction() != null) {
            switch (updateEventUserRequest.getStateAction()) {
                case CANCEL_REVIEW:
                    if (event.getState().equals(EventFullDto.StateEnum.PENDING.toString()) ||
                            event.getState().equals(EventFullDto.StateEnum.CANCELED.toString())) {
                        event.setState(EventFullDto.StateEnum.CANCELED.toString());
                    } else {
                        throw new NotMeetRulesEditionException(Exceptions.EXCEPTION_NOT_MEET_RULES);
                    }
                    break;
                case SEND_TO_REVIEW:
                    if (event.getState().equals(EventFullDto.StateEnum.CANCELED.toString())) {
                        event.setState(EventFullDto.StateEnum.PENDING.toString());
                    } else {
                        throw new NotMeetRulesEditionException(Exceptions.EXCEPTION_NOT_MEET_RULES);
                    }
                    break;
                default:
                    throw new NotMeetRulesEditionException(Exceptions.EXCEPTION_NOT_MEET_RULES);
            }
        }

        eventRepository.save(event);

        return ResponseEntity.ok(getEventFullDto(event, location));
    }

    @Override
    public ResponseEntity<EventFullDto> updateEventAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        log.info(Messages.MESSAGE_UPDATE_EVENT);

        checkDate(updateEventAdminRequest.getEventDate());

        LocationEntity location = null;

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(Exceptions.EXCEPTION_NOT_FOUND));

        if (updateEventAdminRequest.getLocation() != null) {
            location = locationRepository
                    .save(locationMapper.locationToLocationEntity(updateEventAdminRequest.getLocation()));
            event.setLocation(location.getId());
        } else {
            location = locationRepository.findById(event.getLocation()).get();
            updateEventAdminRequest.setLocation(locationMapper.locationEntityToLocation(location));
        }

        eventMapper.updateEventAdminRequestToEvent(event, location.getId(), updateEventAdminRequest);

        if (updateEventAdminRequest.getStateAction() != null) {
            if (!event.getState().equals(EventFullDto.StateEnum.PUBLISHED.toString())
                    && updateEventAdminRequest.getStateAction()
                    .equals(UpdateEventAdminRequest.StateActionEnum.REJECT_EVENT)) {
                event.setState(EventFullDto.StateEnum.CANCELED.toString());
            } else if (event.getState().equals(EventFullDto.StateEnum.PENDING.toString())
                    && updateEventAdminRequest.getStateAction()
                    .equals(UpdateEventAdminRequest.StateActionEnum.PUBLISH_EVENT)) {
                event.setState(EventFullDto.StateEnum.PUBLISHED.toString());
            } else {
                throw new NotMeetRulesEditionException(Exceptions.EXCEPTION_NOT_MEET_RULES);
            }
        }

        return ResponseEntity.ok(getEventFullDto(eventRepository.save(event), location));
    }

    private EventFullDto getEventFullDto(Event event, LocationEntity location) {
        User user = userRepository.findById(event.getInitiator())
                .orElseThrow(() -> new NotFoundException(String.format(Exceptions.EXCEPTION_NOT_FOUND_USER,
                        event.getInitiator())));
        Category category = categoryRepository.findById(event.getCategory())
                .orElseThrow(() -> new NotFoundException(Exceptions.EXCEPTION_NOT_FOUND));

        EventFullDto eventFullDto = eventMapper.eventToEventFullDto(event);

        eventFullDto.setPublishedOn(OffsetDateTime.now().format(DateTimeFormatter.ofPattern(Values.DATE_TIME_PATTERN)));
        eventFullDto.setInitiator(userMapper.userToUserShortDto(user));
        eventFullDto.setCategory(categoryMapper.toCategoryDto(category));
        eventFullDto.setLocation(locationMapper.locationEntityToLocation(location));

        return eventFullDto;
    }

    private EventShortDto getEventShortDto(Event event) {
        EventShortDto eventShortDto = eventMapper.eventToEventShortDto(event);

        User user = userRepository.findById(event.getInitiator())
                .orElseThrow(() -> new NotFoundException(String.format(Exceptions.EXCEPTION_NOT_FOUND_USER, event.getInitiator())));
        Category category = categoryRepository.findById(event.getCategory())
                .orElseThrow(() -> new NotFoundException(Exceptions.EXCEPTION_NOT_FOUND));

        return eventShortDto
                .initiator(userMapper.userToUserShortDto(user))
                .category(categoryMapper.toCategoryDto(category));
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
            throw new MismatchDateException(Exceptions.EXCEPTION_DATE_MISMATCH);
        }
    }

    private void validateEventFields(NewEventDto newEventDto) {
        if (newEventDto.getAnnotation() != null && newEventDto.getAnnotation().trim().isEmpty()) {
            throw new ValidationException(Exceptions.EXCEPTION_FIELD_ANNOTATION_NOT_HAS_SPACE);
        }

        if (newEventDto.getDescription() != null && newEventDto.getDescription().trim().isEmpty()) {
            throw new ValidationException(Exceptions.EXCEPTION_FIELD_DESCRIPTION_NOT_HAS_SPACE);
        }

        if (newEventDto.getTitle() != null && newEventDto.getTitle().trim().isEmpty()) {
            throw new ValidationException(Exceptions.EXCEPTION_FIELD_TITLE_NOT_HAS_SPACE);
        }
    }

    private void validateDateRange(String rangeStart, String rangeEnd) {
        if (rangeStart != null && rangeEnd != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Values.DATE_TIME_PATTERN);

            LocalDateTime start = LocalDateTime.parse(rangeStart, formatter);
            LocalDateTime end = LocalDateTime.parse(rangeEnd, formatter);

            if (start.isAfter(end)) {
                throw new ValidationException(Exceptions.EXCEPTION_WRONG_DATE_RANGE);
            }
        }
    }
}
