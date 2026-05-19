package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.messages.Message;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final SimilarityCalculator calculator;

    @KafkaListener(topics = "stats.user-actions.v1", groupId = "aggregator-group")
    public void consume(UserActionAvro message) {
        log.info(Message.RECEIVED_FROM_KAFKA,
                message.getUserId(), message.getEventId(), message.getActionType());

        try {
            calculator.processUserAction(message);
        } catch (Exception e) {
            log.error(Message.ERROR_PROCESS_ACTION, e);
        }
    }
}