package main.java.ru.practicum.util;

import main.java.ru.practicum.constant.Values;
import main.java.ru.practicum.dto.HitEventRequest;

import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;

public class EwmClient {
    private static final RestClient client = RestClient.create();

    public static void sendEvent(String uri, Long id) {
        client.post().uri(Values.ADDRESS_STATS_SERVER)
                .body(new HitEventRequest(Values.APPLICATION, uri + id, Values.EWM_IP, LocalDateTime.now()))
                .retrieve()
                .toBodilessEntity();
    }

    public static void sendEvents(String uri) {
        client.post().uri(Values.ADDRESS_STATS_SERVER)
                .body(new HitEventRequest(Values.APPLICATION, uri, Values.EWM_IP, LocalDateTime.now()))
                .retrieve()
                .toBodilessEntity();
    }
}
