package ru.practicum.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimilarityProducer {

    private final KafkaTemplate<String, EventSimilarityAvro> similarityKafkaTemplate;

    @Value("${kafka.topics.events-similarity:stats.events-similarity.v1}")
    private String topic;

    public void send(EventSimilarityAvro message) {
        String key = message.getEventA() + "_" + message.getEventB();
        log.info("Sending similarity to Kafka: topic={}, key={}, eventA={}, eventB={}, score={}",
                topic, key, message.getEventA(), message.getEventB(), message.getScore());

        CompletableFuture<SendResult<String, EventSimilarityAvro>> future =
                similarityKafkaTemplate.send(topic, key, message);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Similarity sent successfully: offset={}, partition={}",
                        result.getRecordMetadata().offset(),
                        result.getRecordMetadata().partition());
            } else {
                log.error("Failed to send similarity for key={}: {}", key, ex.getMessage(), ex);
            }
        });
    }
}