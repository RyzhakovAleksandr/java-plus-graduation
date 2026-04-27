package ru.practicum.util;

import ru.practicum.constant.Values;
import ru.practicum.dto.HitEventRequest;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.MaxAttemptsRetryPolicy;

import java.time.LocalDateTime;

@Component
public class EwmClient {
    private final RestClient client;
    private final DiscoveryClient discoveryClient;
    private final RetryTemplate retryTemplate;
    private final String statsServiceId = "stats-server";

    public EwmClient(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
        this.client = RestClient.create();

        // Настройка retry
        this.retryTemplate = new RetryTemplate();
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(3000L);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
        MaxAttemptsRetryPolicy retryPolicy = new MaxAttemptsRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        retryTemplate.setRetryPolicy(retryPolicy);
    }

    private ServiceInstance getInstance() {
        return retryTemplate.execute(context -> {
            var instances = discoveryClient.getInstances(statsServiceId);
            if (instances.isEmpty()) {
                throw new RuntimeException("Сервис статистики не найден в Eureka: " + statsServiceId);
            }
            return instances.getFirst();
        });
    }

    private String getStatsServerUrl() {
        ServiceInstance instance = getInstance();
        return "http://" + instance.getHost() + ":" + instance.getPort() + "/hit";
    }

    public void sendEvent(String uri, Long id) {
        client.post()
                .uri(getStatsServerUrl())
                .body(new HitEventRequest(Values.APPLICATION, uri + id, Values.EWM_IP, LocalDateTime.now()))
                .retrieve()
                .toBodilessEntity();
    }

    public void sendEvents(String uri) {
        client.post()
                .uri(getStatsServerUrl())
                .body(new HitEventRequest(Values.APPLICATION, uri, Values.EWM_IP, LocalDateTime.now()))
                .retrieve()
                .toBodilessEntity();
    }
}