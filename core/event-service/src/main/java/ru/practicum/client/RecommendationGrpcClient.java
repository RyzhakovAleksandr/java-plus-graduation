package ru.practicum.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.practicum.constant.Message;
import ru.practicum.ewm.stats.proto.*;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationGrpcClient {

    @GrpcClient("analyzer")
    private RecommendationsControllerGrpc.RecommendationsControllerBlockingStub analyzerStub;

    @GrpcClient("collector")
    private UserActionControllerGrpc.UserActionControllerBlockingStub collectorStub;

    public void sendUserAction(long userId, long eventId, ActionTypeProto actionType) {
        UserActionProto request = UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(actionType)
                .setTimestamp(com.google.protobuf.Timestamp.newBuilder()
                        .setSeconds(System.currentTimeMillis() / 1000)
                        .setNanos((int) ((System.currentTimeMillis() % 1000) * 1_000_000))
                        .build())
                .build();

        collectorStub.collectUserAction(request);
        log.info(Message.SEND_USER_ACTION, userId, eventId, actionType);
    }

    public List<Long> getRecommendations(long userId, int maxResults) {
        UserPredictionsRequestProto request = UserPredictionsRequestProto.newBuilder()
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();

        Iterator<RecommendedEventProto> iterator = analyzerStub.getRecommendationsForUser(request);

        return StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(iterator, 0),
                        false
                )
                .map(RecommendedEventProto::getEventId)
                .collect(Collectors.toList());
    }

    public List<Long> getSimilarEvents(long eventId, long userId, int maxResults) {
        SimilarEventsRequestProto request = SimilarEventsRequestProto.newBuilder()
                .setEventId(eventId)
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();

        Iterator<RecommendedEventProto> iterator = analyzerStub.getSimilarEvents(request);

        return StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(iterator, 0),
                        false
                )
                .map(RecommendedEventProto::getEventId)
                .collect(Collectors.toList());
    }

    public Map<Long, Double> getInteractionsCount(List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return Map.of();
        }

        InteractionsCountRequestProto request = InteractionsCountRequestProto.newBuilder()
                .addAllEventId(eventIds)
                .build();

        Iterator<RecommendedEventProto> iterator = analyzerStub.getInteractionsCount(request);

        return StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(iterator, 0),
                        false
                )
                .collect(Collectors.toMap(
                        RecommendedEventProto::getEventId,
                        RecommendedEventProto::getScore
                ));
    }
}