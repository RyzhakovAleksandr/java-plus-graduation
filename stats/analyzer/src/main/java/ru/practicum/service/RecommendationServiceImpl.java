package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.messeges.Message;
import ru.practicum.repository.EventSimilarityRepository;
import ru.practicum.repository.UserActionRepository;
import ru.practicum.model.EventSimilarityEntity;
import ru.practicum.model.UserActionEntity;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final UserActionRepository actionRepository;
    private final EventSimilarityRepository similarityRepository;

    @Override
    public List<EventSimilarityEntity> getSimilarEvents(Long eventId, Long userId, int maxResults) {
        log.debug(Message.FIND_SIMILAR_EVENTS, eventId, userId);

        List<EventSimilarityEntity> similarEvents = similarityRepository.findSimilarEvents(eventId);

        Set<Long> interactedEvents = actionRepository.findUserInteractedEvents(userId);

        return similarEvents.stream()
                .filter(similarity -> !interactedEvents.contains(getOtherEventId(similarity, eventId)))
                .sorted((firstEvent, secondEvent) -> Double.compare(secondEvent.getScore(), firstEvent.getScore()))
                .limit(maxResults)
                .toList();
    }

    @Override
    public List<Long> getRecommendationsForUser(Long userId, int maxResults) {
        log.debug(Message.GET_RECOMMENDATION_FOR_USER, userId);

        List<UserActionEntity> recentActions = actionRepository.findRecentActionsByUserId(userId);

        if (recentActions.isEmpty()) {
            log.debug(Message.USER_DONT_HAVE_ACTION);
            return Collections.emptyList();
        }

        List<Long> eventIds = recentActions.stream()
                .map(UserActionEntity::getEventId)
                .toList();

        List<EventSimilarityEntity> allSimilar = similarityRepository.findSimilarEventsByEventIds(eventIds);

        Map<Long, List<EventSimilarityEntity>> similarByEventId = allSimilar.stream()
                .collect(Collectors.groupingBy(similarity ->
                        eventIds.contains(similarity.getEventA()) ? similarity.getEventA() : similarity.getEventB()
                ));

        Set<Long> interactedEvents = actionRepository.findUserInteractedEvents(userId);

        Map<Long, Double> candidateScores = new HashMap<>();

        for (UserActionEntity action : recentActions) {
            Long eventId = action.getEventId();
            List<EventSimilarityEntity> similar = similarByEventId.getOrDefault(eventId, Collections.emptyList());
            double weight = action.getWeight();

            for (EventSimilarityEntity similarity : similar) {
                Long candidateId = similarity.getEventA().equals(eventId)
                        ? similarity.getEventB() : similarity.getEventA();
                if (!interactedEvents.contains(candidateId)) {
                    candidateScores.merge(candidateId, similarity.getScore() * weight, Double::sum);
                }
            }
        }

        return candidateScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(maxResults)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, Double> getInteractionsCount(List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return Map.of();
        }

        List<Object[]> results = actionRepository.getTotalWeightForEvents(eventIds);

        return results.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Double) row[1]
                ));
    }

    private Long getOtherEventId(EventSimilarityEntity similarEvent, Long eventId) {
        return similarEvent.getEventA().equals(eventId) ? similarEvent.getEventB() : eventId;
    }
}
