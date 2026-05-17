package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.mapper.SimilarityMapper;
import ru.practicum.mapper.UserActionMapper;
import ru.practicum.model.EventSimilarityEntity;
import ru.practicum.model.UserActionEntity;
import ru.practicum.repository.EventSimilarityRepository;
import ru.practicum.repository.UserActionRepository;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final UserActionRepository userActionRepository;
    private final EventSimilarityRepository similarityRepository;
    private final UserActionMapper userActionMapper;
    private final SimilarityMapper similarityMapper;

    @KafkaListener(
            topics = "${kafka.topic.user-actions:stats.user-actions.v1}",
            containerFactory = "userActionKafkaListenerContainerFactory"
    )
    @Transactional
    public void consumeUserAction(List<ConsumerRecord<String, UserActionAvro>> records) {
        for (ConsumerRecord<String, UserActionAvro> record : records) {
            UserActionAvro avro = record.value();

            UserActionEntity existing = userActionRepository
                    .findByUserIdAndEventId(avro.getUserId(), avro.getEventId())
                    .orElse(null);

            if (existing != null) {
                log.info("Existing entity: userId={}, eventId={}, weight={}, actionType={}",
                        existing.getUserId(), existing.getEventId(),
                        existing.getWeight(), existing.getActionType());
            }

            UserActionEntity entity;
            if (existing != null) {
                double newWeight = userActionMapper.toWeight(avro.getActionType());
                if (newWeight > existing.getWeight()) {
                    existing.setWeight(newWeight);
                    existing.setActionType(userActionMapper.toActionType(avro.getActionType()));
                    existing.setLastActionTime(Instant.ofEpochMilli(avro.getTimestamp()));
                    log.info("Updating entity: userId={}, eventId={}, oldWeight={}, newWeight={}",
                            existing.getUserId(), existing.getEventId(), existing.getWeight(), newWeight);
                } else {
                    log.debug("Skipping update: newWeight {} <= oldWeight {}", newWeight, existing.getWeight());
                }
                entity = existing;
            } else {
                entity = userActionMapper.toEntity(avro);
            }

            userActionRepository.save(entity);
        }
    }

    @KafkaListener(
            topics = "${kafka.topic.similarity:stats.events-similarity.v1}",
            containerFactory = "similarityKafkaListenerContainerFactory"
    )
    public void consumeSimilarity(List<ConsumerRecord<String, EventSimilarityAvro>> records) {
        for (ConsumerRecord<String, EventSimilarityAvro> record : records) {
            log.info("Received similarity: key={}, eventA={}, eventB={}, score={}",
                    record.key(),
                    record.value().getEventA(),
                    record.value().getEventB(),
                    record.value().getScore());

            EventSimilarityAvro avro = record.value();

            EventSimilarityEntity entity = similarityRepository
                    .findByEventAAndEventB(avro.getEventA(), avro.getEventB())
                    .orElse(null);

            if (entity == null) {
                entity = similarityMapper.toEntity(avro);
            } else {
                similarityMapper.updateEntity(entity, avro);
            }

            similarityRepository.save(entity);
            log.debug("Saved similarity: eventA={}, eventB={}, score={}",
                    entity.getEventA(), entity.getEventB(), entity.getScore());
        }
    }
}