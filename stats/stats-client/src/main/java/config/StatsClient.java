package config;

import dto.EndpointHitDto;
import dto.ViewStats;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatsClient {

    @Value("${stats.client.baseUrl:http://localhost:9090}")
    private String baseUrl;

    private RestClient client;

    @PostConstruct
    public void init() {
        this.client = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

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