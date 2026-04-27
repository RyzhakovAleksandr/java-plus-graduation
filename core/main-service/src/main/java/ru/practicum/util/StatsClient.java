package ru.practicum.util;

import dto.EndpointHitDto;
import dto.ViewStats;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@FeignClient(name= "stats-server")
public interface StatsClient {
    @PostMapping("/hit")
    ResponseEntity<String> saveHit(@RequestBody EndpointHitDto request);

    @GetMapping("/stats")
    ResponseEntity<List<ViewStats>> getStats(
            @RequestParam @DateTimeFormat(pattern = constant.Values.DATE_TIME_PATTERN) LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = constant.Values.DATE_TIME_PATTERN) LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique);
}
