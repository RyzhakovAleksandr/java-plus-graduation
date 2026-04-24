package java;

import dto.EndpointHitDto;
import dto.ViewStats;

import lombok.RequiredArgsConstructor;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsClient {
    private final RestClient client;

    public void saveHit(EndpointHitDto hitDto) {
        client.post()
                .uri("/hit")
                .body(hitDto)
                .retrieve()
                .toBodilessEntity();
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        UriBuilder builder = UriComponentsBuilder.fromPath("/stats")
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("unique", unique);

        if (uris != null) {
            builder.queryParam("uris", uris);
        }

        return client.get()
                .uri(builder.toUriString())
                .retrieve()
                .body(new ParameterizedTypeReference<List<ViewStats>>() {});
    }
}
