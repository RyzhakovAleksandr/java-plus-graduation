package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.UserActionAvro;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaUserActionProducer {

    private final KafkaTemplate<Long, UserActionAvro> kafkaTemplate;

    @Value("${kafka.topics.user-actions:stats.user-actions.v1}")
    private String topic;

    public void send(UserActionAvro message) {
        Long key = message.getUserId();
        log.info("Sending to Kafka: topic={}, key={}, eventId={}, actionType={}",
                topic, key, message.getEventId(), message.getActionType());

        kafkaTemplate.send(topic, key, message)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.debug("Message sent: partition={}, offset={}",
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to send message", ex);
                    }
                });
    }
}