package ru.practicum.repository;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Getter
public class SimilarityStore {

    private final Map<Long, Map<Long, Double>> userEventWeights = new ConcurrentHashMap<>();

    private final Map<Long, Set<Long>> userToEvents = new ConcurrentHashMap<>();

    private final Map<Long, Double> eventSums = new ConcurrentHashMap<>();

    private final Map<Long, Map<Long, Double>> minWeightsSums = new ConcurrentHashMap<>();

    private static final Map<ActionTypeAvro, Double> WEIGHTS = Map.of(
            ActionTypeAvro.VIEW, 0.4,
            ActionTypeAvro.REGISTER, 0.8,
            ActionTypeAvro.LIKE, 1.0
    );

    @PostConstruct
    public void init() {
        for (Map.Entry<Long, Map<Long, Double>> entry : userEventWeights.entrySet()) {
            Long eventId = entry.getKey();
            for (Long userId : entry.getValue().keySet()) {
                addUserEvent(userId, eventId);
            }
        }
    }

    public Double getWeight(ActionTypeAvro actionType) {
        return WEIGHTS.get(actionType);
    }

    public void putMinWeightSum(long eventA, long eventB, double sum) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);
        minWeightsSums.computeIfAbsent(first, k -> new ConcurrentHashMap<>()).put(second, sum);
    }

    public Double getMinWeightSum(long eventA, long eventB) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);
        Map<Long, Double> innerMap = minWeightsSums.get(first);
        if (innerMap == null) return 0.0;
        return innerMap.getOrDefault(second, 0.0);
    }


    public void addUserEvent(long userId, long eventId) {
        userToEvents.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(eventId);
    }

    public Set<Long> getUserEvents(long userId) {
        return userToEvents.getOrDefault(userId, Set.of());
    }

    public Double getUserWeightForEvent(long userId, long eventId) {
        Map<Long, Double> eventWeights = userEventWeights.get(eventId);
        if (eventWeights == null) return null;
        return eventWeights.get(userId);
    }

    public void updateUserWeight(long eventId, long userId, double newWeight) {
        userEventWeights.computeIfAbsent(eventId, k -> new ConcurrentHashMap<>()).put(userId, newWeight);
    }

    public void updateEventSum(long eventId, double delta) {
        eventSums.merge(eventId, delta, Double::sum);
    }

    public double getEventSum(long eventId) {
        return eventSums.getOrDefault(eventId, 0.0);
    }
}