package ru.practicum.client;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.dto.EventRequestStatusRequest;
import ru.practicum.dto.EventRequestStatusUpdateResult;
import ru.practicum.dto.ParticipationRequestDto;
import org.springframework.cloud.openfeign.FeignClient;

import java.util.List;

@FeignClient(name = "request-service", fallbackFactory = RequestClientFallbackFactory.class)
public interface RequestClient {

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    List<ParticipationRequestDto> getEventParticipants(
            @PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId);

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    EventRequestStatusUpdateResult changeRequestStatus(
            @PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId,
            @RequestBody EventRequestStatusRequest request);

    @GetMapping("/events/{eventId}/confirmed-count")
    Long getConfirmedRequestsCount(@PathVariable("eventId") Long eventId);

    @GetMapping("/users/{userId}/events/{eventId}/visited")
    Boolean hasUserVisitedEvent(
            @PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId);
}
