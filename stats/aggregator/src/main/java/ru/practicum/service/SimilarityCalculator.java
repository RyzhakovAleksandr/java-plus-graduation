package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.producer.SimilarityProducer;
import ru.practicum.repository.SimilarityStore;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimilarityCalculator {

    private final SimilarityStore store;
    private final SimilarityProducer producer;

    public void processUserAction(UserActionAvro action) {
        long eventId = action.getEventId();
        long userId = action.getUserId();
        double newWeight = store.getWeight(action.getActionType());
        log.info("newWeight for action {} is {}", action.getActionType(), newWeight);

        log.debug("Processing user action: userId={}, eventId={}, newWeight={}",
                userId, eventId, newWeight);

        Double oldWeight = store.getUserWeightForEvent(userId, eventId);

        if (oldWeight != null && oldWeight >= newWeight) {
            log.debug("Weight not increased for user={}, event={}, old={}, new={}, skipping",
                    userId, eventId, oldWeight, newWeight);
            return;
        }

        double delta = (oldWeight == null) ? newWeight : newWeight - oldWeight;
        log.debug("Delta for event {}: {}", eventId, delta);

        store.updateUserWeight(eventId, userId, newWeight);

        store.addUserEvent(userId, eventId);

        double oldSumA = store.getEventSum(eventId);
        double newSumA = oldSumA + delta;
        store.updateEventSum(eventId, delta);


        log.debug("Updated event sum for event {}: {} -> {}", eventId, oldSumA, newSumA);

        recalculateSimilarities(eventId, userId, oldWeight, newWeight, action.getTimestamp());
    }

    private void recalculateSimilarities(long eventA, long userId, Double oldWeightA,
                                         double newWeightA, long timestamp) {
        double sumA = store.getEventSum(eventA);
        if (sumA == 0.0) {
            log.debug("sumA is 0 for event {}, skipping", eventA);
            return;
        }

        Set<Long> userEvents = store.getUserEvents(userId);
        log.debug("User {} has interacted with events: {}", userId, userEvents);

        int calculatedCount = 0;

        for (Long eventB : userEvents) {
            if (eventA == eventB) continue;

            Double weightB = store.getUserWeightForEvent(userId, eventB);
            if (weightB == null) {
                log.debug("User {} has no weight for event {}, skipping pair ({}, {})",
                        userId, eventB, eventA, eventB);
                continue;
            }

            double sumB = store.getEventSum(eventB);
            if (sumB == 0.0) {
                log.debug("sumB is 0 for event {}, skipping pair ({}, {})",
                        eventB, eventA, eventB);
                continue;
            }

            double oldMin = (oldWeightA == null) ? 0.0 : Math.min(oldWeightA, weightB);
            double newMin = Math.min(newWeightA, weightB);
            double deltaMin = newMin - oldMin;

            log.debug("Pair ({}, {}): oldMin={}, newMin={}, deltaMin={}",
                    eventA, eventB, oldMin, newMin, deltaMin);

            double oldSMin = store.getMinWeightSum(eventA, eventB);
            double newSMin = oldSMin + deltaMin;
            store.putMinWeightSum(eventA, eventB, newSMin);

            double similarity = newSMin / (Math.sqrt(sumA) * Math.sqrt(sumB));

            if (Double.isNaN(similarity)) {
                log.warn("Similarity is NaN for pair ({}, {}), setting to 0", eventA, eventB);
                similarity = 0.0;
            }

            log.debug("Similarity calculated: eventA={}, eventB={}, score={}",
                    Math.min(eventA, eventB), Math.max(eventA, eventB), similarity);

            EventSimilarityAvro message = EventSimilarityAvro.newBuilder()
                    .setEventA(Math.min(eventA, eventB))
                    .setEventB(Math.max(eventA, eventB))
                    .setScore(similarity)
                    .setTimestamp(timestamp)
                    .build();

            producer.send(message);
            calculatedCount++;
        }

        log.debug("Recalculated {} similarities for eventA={} and userId={}",
                calculatedCount, eventA, userId);
    }
}