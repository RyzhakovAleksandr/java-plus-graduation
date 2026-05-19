package ru.practicum.service;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.ewm.stats.proto.*;
import ru.practicum.messeges.Message;
import ru.practicum.model.EventSimilarityEntity;

import java.util.List;
import java.util.Map;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class RecommendationsGrpcService extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {
    private final RecommendationService recommendationService;

    @Override
    public void getSimilarEvents(SimilarEventsRequestProto request,
                                 StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            long eventId = request.getEventId();
            long userId = request.getUserId();
            int maxResults = request.getMaxResults();

            log.info(Message.GRPC_GET_SIMILAR_EVENT,
                    eventId, userId, maxResults);

            List<EventSimilarityEntity> similarEvents =
                    recommendationService.getSimilarEvents(eventId, userId, maxResults);

            for (EventSimilarityEntity entity : similarEvents) {
                long recommendationEvent = entity.getEventA().equals(eventId)
                        ? entity.getEventB() : entity.getEventA();

                RecommendedEventProto response = RecommendedEventProto.newBuilder()
                        .setEventId(recommendationEvent)
                        .setScore(entity.getScore())
                        .build();
                responseObserver.onNext(response);
            }

            responseObserver.onCompleted();
            log.info(Message.SEND_SIMILARITY_EVENT, similarEvents.size(), eventId);

        } catch (Exception ex) {
            log.error(Message.ERROR_GRPC_GET_SIMILARITY, ex);
            responseObserver.onError(ex);
        }

    }

    @Override
    public void getRecommendationsForUser(UserPredictionsRequestProto request,
                                          StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            long userId = request.getUserId();
            int maxResults = request.getMaxResults();

            log.info(Message.GRPC_RECOMMENDATION_FOR_USER,
                    userId, maxResults);

            List<Long> recommendations = recommendationService.getRecommendationsForUser(userId, maxResults);
            Map<Long, Double> scores = recommendationService.getInteractionsCount(recommendations);

            for (Long eventId : recommendations) {
                RecommendedEventProto response = RecommendedEventProto.newBuilder()
                        .setEventId(eventId)
                        .setScore(scores.getOrDefault(eventId, 0.0))
                        .build();
                responseObserver.onNext(response);
            }

            responseObserver.onCompleted();
            log.info(Message.SEND_RECOMMENDATION_FOR_USER, recommendations.size(), userId);

        } catch (Exception ex) {
            log.error(Message.ERROR_GRPC_RECOMMENDATION, ex);
            responseObserver.onError(ex);
        }
    }

    @Override
    public void getInteractionsCount(InteractionsCountRequestProto request,
                                     StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            List<Long> eventIds = request.getEventIdList();

            if (eventIds.isEmpty()) {
                responseObserver.onCompleted();
                return;
            }

            log.info(Message.GRPC_INTERACTION, eventIds.size());

            long startTime = System.currentTimeMillis();

            Map<Long, Double> scores = recommendationService.getInteractionsCount(eventIds);

            long duration = System.currentTimeMillis() - startTime;
            log.info(Message.TIME_QUERY, duration, eventIds.size());

            for (Long eventId : eventIds) {
                RecommendedEventProto response = RecommendedEventProto.newBuilder()
                        .setEventId(eventId)
                        .setScore(scores.getOrDefault(eventId, 0.0))
                        .build();
                responseObserver.onNext(response);
            }

            responseObserver.onCompleted();
            log.info(Message.SEND_INTERACTION, eventIds.size());

        } catch (Exception ex) {
            log.error(Message.ERROR_GRPC_INTERACTION, ex);
            responseObserver.onError(ex);
        }
    }
}