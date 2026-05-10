package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;

import java.util.List;

@FeignClient(name = "event-service", fallbackFactory = EventClientFallbackFactory.class)
public interface EventClient {
    @GetMapping("/events/{id}")
    EventFullDto getEvent(@PathVariable("id") Long id);

    @GetMapping("/events/by-ids")
    List<EventShortDto> getEventsByIds(@RequestParam("ids") List<Long> ids);

    @GetMapping("/events/{id}/short")
    EventShortDto getEventShort(@PathVariable("id") Long id);

    @GetMapping("/events/by-category/{categoryId}/exists")
    boolean hasEventsByCategory(@PathVariable("categoryId") Long categoryId);

    @GetMapping("/admin/events/{id}")
    EventFullDto getEventInternal(@PathVariable("id") Long id);
}
