package main.java.ru.practicum.service;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import constant.Messages;

import dto.EndpointHitDto;
import dto.ViewStats;

import main.java.ru.practicum.mapper.StatsMapper;
import main.java.ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    private final StatsMapper mapper;

    @Override
    public void saveHit(EndpointHitDto hitDto) {
        statsRepository.save(mapper.endpointHitDtoToEndpointHit(hitDto));
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start.isAfter(end)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, Messages.DATE_EXCEPTION);
        }

        if (uris != null) {
            return statsRepository.getStats(start, end, uris.toArray(String[]::new), unique);
        }

        return statsRepository.getStatsWithoutUris(start, end, unique);
    }
}
