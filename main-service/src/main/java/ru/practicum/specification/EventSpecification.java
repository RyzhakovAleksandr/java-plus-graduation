package main.java.ru.practicum.specification;

import jakarta.persistence.criteria.Predicate;

import main.java.ru.practicum.constant.Values;
import main.java.ru.practicum.dto.GetEventsForAdminRequest;
import main.java.ru.practicum.dto.GetEventsRequest;
import main.java.ru.practicum.persistence.entity.Event;
import main.java.ru.practicum.persistence.repository.EventRepository;
import main.java.ru.practicum.persistence.status.StatusSort;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class EventSpecification {
    public Page<Event> getPagesFromGetEventsRequest(GetEventsRequest request, EventRepository eventRepository) {
        Specification<Event> spec = (r, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getText() != null) {
                predicates.add(cb.like(r.get("annotation"), "%" + request.getText() + "%"));
            }

            if (request.getCategories() != null) {
                predicates.add(cb.in(r.get("category")).value(request.getCategories()));
            }

            if (request.getPaid() != null) {
                predicates.add(cb.equal(r.get("paid"), request.getPaid()));
            }

            if (request.getRangeStart() != null) {
                OffsetDateTime dateTime = ZonedDateTime.of(
                        LocalDateTime.parse(request.getRangeStart(), DateTimeFormatter.ofPattern(Values.DATE_TIME_PATTERN)),
                        ZoneId.systemDefault()).toOffsetDateTime();
                predicates.add(cb.greaterThanOrEqualTo(r.get("eventDate"), dateTime));
            }

            if (request.getRangeEnd() != null) {
                OffsetDateTime dateTime = ZonedDateTime.of(
                        LocalDateTime.parse(request.getRangeEnd(), DateTimeFormatter.ofPattern(Values.DATE_TIME_PATTERN)),
                        ZoneId.systemDefault()).toOffsetDateTime();
                predicates.add(cb.lessThanOrEqualTo(r.get("eventDate"), dateTime));
            }

            if (request.getOnlyAvailable()) {
                predicates.add(cb.greaterThan(r.get("participantLimit"), 0));
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };

        Pageable pageable;

        if (request.getSort() != null) {
            String sort;

            if (request.getSort().equals(StatusSort.EVENT_DATE.toString())) {
                sort = "createdAt";
            } else {
                sort = "views";
            }

            pageable = PageRequest.of(
                    request.getFrom() / request.getSize(),
                    request.getSize(),
                    Sort.by(sort).descending()
            );
        }


        pageable = PageRequest.of(
                request.getFrom() / request.getSize(),
                request.getSize()
        );

        return eventRepository.findAll(spec, pageable);
    }

    public Page<Event> getPagesFromGetEventsForAdminRequest(GetEventsForAdminRequest request, EventRepository eventRepository) {
        Specification<Event> spec = (r, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getUsers() != null) {
                predicates.add(cb.in(r.get("initiator")).value(request.getUsers()));
            }

            if (request.getStates() != null) {
                predicates.add(cb.in(r.get("state")).value(request.getStates()));
            }

            if (request.getStates() != null) {
                predicates.add(cb.in(r.get("category")).value(request.getCategories()));
            }

            if (request.getRangeStart() != null) {
                OffsetDateTime dateTime = ZonedDateTime.of(
                        LocalDateTime.parse(request.getRangeStart(), DateTimeFormatter.ofPattern(Values.DATE_TIME_PATTERN)),
                        ZoneId.systemDefault()).toOffsetDateTime();
                predicates.add(cb.greaterThanOrEqualTo(r.get("eventDate"), dateTime));
            }

            if (request.getRangeEnd() != null) {
                OffsetDateTime dateTime = ZonedDateTime.of(
                        LocalDateTime.parse(request.getRangeEnd(), DateTimeFormatter.ofPattern(Values.DATE_TIME_PATTERN)),
                        ZoneId.of("Europe/Moscow")).toOffsetDateTime();
                predicates.add(cb.lessThanOrEqualTo(r.get("eventDate"), dateTime));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(
                request.getFrom() / request.getSize(),
                request.getSize()
        );

        return eventRepository.findAll(spec, pageable);
    }
}
